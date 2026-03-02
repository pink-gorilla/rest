(ns rest.oauth2
  (:require
   [promesa.core :as p]
   [martian.core :as martian]
   [martian.interceptors :as interceptors]
   [martian.clj-http :as martian-http]
   [token.oauth2.core :refer [get-access-token]]
   [token.oauth2.provider :refer [oauth2-auth-header-prefix]]))

(defn is-exception? [value]
  (instance? Throwable value))

(defn add-authentication-header [token provider]
  {:name ::add-authentication-header
   :enter (fn [ctx]
            (let [prefix (oauth2-auth-header-prefix {:provider provider})
                  ; token-prefix could be: "Token: " "Bearer "
                  access-token (p/await (get-access-token token provider))]
              ;(println "adding auth token: " access-token)
              (if (is-exception? access-token)
                (throw access-token)
                (assoc-in ctx
                          [:request :headers "Authorization"]
                          (str prefix " " access-token)))))})

(defn interceptors [token provider]
  {:interceptors
   (concat
    martian/default-interceptors
    [(add-authentication-header token provider)
     interceptors/default-encode-body
     interceptors/default-coerce-response
     martian-http/perform-request])})

(defn martian-oauth2 [token provider base-url endpoints]
  (let [m (martian-http/bootstrap base-url endpoints (interceptors token provider))]
    m))
