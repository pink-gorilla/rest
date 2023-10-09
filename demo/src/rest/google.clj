(ns rest.google
  (:require
   [clojure.pprint :refer [print-table]]
   [taoensso.timbre :as timbre :refer [info error]]
   [martian.core :as martian]
   [modular.oauth2.token.refresh :refer [refresh-access-token]]
   [modular.rest.martian.google :refer [martian-googleapis martian-google-sheets martian-google-search]]))

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
    (info "request " endpoint ":" (pr-str r)
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

(def m (martian-googleapis))
(def s (martian-google-sheets))
(def S (martian-google-search))

(defn google []
  ; this function can be evaled entirely via clj-cli
    ; or you can connect via nrepl port 9100 and eval 
    ; one by one
  @(refresh-access-token :google)

  ; userinfo
  (info "google/userinfo: " (-> (martian/response-for m :userinfo {}) :body))

  ; google drive
  (->> (martian/response-for m :drive-files-list {})
       :body
       :files
       (map #(dissoc % :kind :id :mimeType))
       (print-table))

  (show-request s :sheet-edit modify-request)

  (->> (martian/response-for s :sheet-edit modify-request)
       :body
       (info))

  (->> (martian/response-for S :search {:q "clojure" :num 10})
       :body
       (info))

  ;
  )
