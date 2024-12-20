(ns rest.google
  (:require
   [clojure.pprint :refer [print-table]]
   [promesa.core :as p]
   [martian.core :as martian]
   [token.oauth2.core :refer [get-access-token]]
   [rest.provider.google :refer [martian-googleapis martian-google-sheets martian-google-search]]
   [modular.system]))

(def modify-request
  {; url
   :id  "1oO-UlrkEwaED_fKcNm27lIN9EdcbwF8iCS5UiYFOnk8"
   :where "/values/Sheet1!A1:D5"
   ; qp
   :valueInputOption "USER_ENTERED"
   ; body
   :range "Sheet1!A1:D5"
   :majorDimension "ROWS"
   :values [["Item" "Cost" "Stocked" "Ship Date"]
            ["Wheel" "$320.50" "4" "3/1/2016"]
            ["Door" "$15" "2" "3/15/2016"]
            ["Engine" "$100" "1" "3/20/2016"]
            ["Totals" "=SUM(B2:B4)" "=SUM(C2:C4)" "=MAX(D2:D4)"]]})


(defn show-request [m endpoint params]
  (let [r (martian/request-for m endpoint params)]
    (println "request " endpoint ":" (pr-str r)
           ;"body: " (slurp (:body r))
          )))

;(defn post-cells []
; (post-request 
;     :google
;     "https://sheets.googleapis.com/v4/spreadsheets/1oO-UlrkEwaED_fKcNm27lIN9EdcbwF8iCS5UiYFOnk8:batchUpdate"
;      {:requests [{:range "A1:B2" 
;                   :values [["Test" "5"]
;                            ["bodrum" "4"]]}]}
;   ))




(def this (modular.system/system :oauth2))

(keys this)


 (p/await (get-access-token this :google))


(def m (martian-googleapis this))
(def s (martian-google-sheets this))
(def S (martian-google-search this))


(martian/request-for m :userinfo {})
;; => {:method :get,
;;     :url "https://www.googleapis.com/oauth2/v2/userinfo",
;;     :headers
;;     {"Authorization"
;;      "Bearer ya29.a0AeDClZCC_nfUCpZgs6TA7UYRyrUJjNlE4Q1CTtwFPC2HUebyjme8QuXqsdfZiCe4oC-jHXO-rWZio8sMvq8V_5GPLImC-3tgzlxSKcKAHLqokzIZ5S8ccoKIJ4qL42Y5u3dK4vrGCZOArU-6YJvX5MpezggovczpNyRhGj1EuwdHEoHFKb_qZhSVrNCi87hoR4LHDb2DxUNQaCgYKAcoSARASFQHGX2MiT7UHSLl3pJ4-f_hzPZyvsA0211",
;;      "Accept" "application/json"},
;;     :as :text}


(-> (martian/response-for m :userinfo {}) :body)

; google drive
(->> (martian/response-for m :drive-files-list {})
     :body
     :files
     (map #(dissoc % :kind :id :mimeType))
     (print-table))




(show-request s :sheet-edit modify-request)

(->> (martian/response-for s :sheet-edit modify-request)
     :body)

(->> (martian/response-for S :search {:q "clojure" :num 10})
     :body)
