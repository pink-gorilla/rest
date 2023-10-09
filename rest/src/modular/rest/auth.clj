(ns modular.rest.auth
  (:require
   [modular.oauth2.base64 :refer [base64-encode]]))

(defn auth-header-basic [username password]
  {"Authorization" (str "Basic " (base64-encode (str username ":" password)))})
