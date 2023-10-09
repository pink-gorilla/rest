(ns demo
  (:require
   ; martian apis
   [rest.google :refer [google]]
   [rest.github :refer [github]]
   [rest.xero :refer [xero]]
   [rest.pets :refer [pets]]
   [rest.woo :refer [woo]]
   ; [rest.wordpress :refer [wordpress]]
   ; rest via libs
   [rest.email :refer [email]]
   [rest.telegram :refer [telegram]]
   ; helper functions  
   [rest.schema :refer [infer-schema]]
   [rest.paging :refer [paging-xero]]
   [rest.validate :refer [validate]]
   ))


(defn make-requests [{:keys [provider]}]
  ; cli entry point
  (case provider
    :xero (xero)
    :google (google)
    :github (github)
    :email (email)
    :telegram (telegram)
    :woo (woo)
    :pets (pets)
 ;   :wordpress (wordpress)
    :schema (infer-schema)
    :paging (paging-xero)
    :validate (validate)
    ;
    ))










