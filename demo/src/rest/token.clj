(ns rest.token
  (:require
   [promesa.core :as p]
   [martian.clj-http :as martian-http]
   [martian.yaml :refer [yaml->edn]]
   [token.oauth2.core :refer [get-access-token get-provider-client-id]]
   [modular.system]
   [modular.persist.edn :refer [pprint-str]]
   
   ))



(def this (modular.system/system :oauth2))

(keys this)

(get-in this [:providers :google])

(keys (:providers this)) 


(p/await (get-access-token this :google))


(get-provider-client-id this :google)


; https://github.com/APIs-guru/openapi-directory/tree/main/APIs


(def m (martian-http/bootstrap-openapi "https://pedestal-api.herokuapp.com/swagger.json"))

(def yaml-xero 
  "https://github.com/APIs-guru/openapi-directory/blob/main/APIs/xero.com/xero_accounting/2.9.4/openapi.yaml"
  )


(martian-http/bootstrap-openapi yaml-xero)

;; => Execution error (ScannerException) at org.yaml.snakeyaml.scanner.ScannerImpl/fetchValue (ScannerImpl.java:870).
;;    mapping values are not allowed here
;;     in 'string', line 219, column 75:
;;         ... age-responsive" style="word-wrap: break-word;">
;;                                             ^
;;    


(->> (slurp "resources/xero.yaml")
    (yaml->edn) 
    pprint-str
    (spit "resources/xero.edn"))


(->> (slurp "resources/calendar.yaml")
     (yaml->edn)
     pprint-str
     (spit "resources/calendar.edn"))




