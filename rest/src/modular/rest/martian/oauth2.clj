(ns modular.rest.martian.oauth2
  (:require
   ;[clj-http.client :as http]
   [martian.core :as martian]
   [martian.interceptors :as interceptors]
   [martian.clj-http :as martian-http]
   [modular.oauth2.token.store :refer [load-token]]
   [modular.oauth2.config :as config]))

; token-prefix could be:
; "Token: "
; "Bearer "

(defn add-authentication-header [provider]
  {:name ::add-authentication-header
   :enter (fn [ctx]
            (let [token-prefix (config/token-prefix provider)
                  token (load-token provider)
                  access-token (:access-token token)]
              (assoc-in ctx
                        [:request :headers "Authorization"]
                        (str token-prefix access-token))))})

(defn interceptors [provider]
  {:interceptors
   (concat
    martian/default-interceptors
    [(add-authentication-header provider)
     interceptors/default-encode-body
     interceptors/default-coerce-response
     martian-http/perform-request])})

(defn martian-oauth2 [provider base-url endpoints]
  (let [m (martian-http/bootstrap base-url endpoints (interceptors provider))]
    m))
