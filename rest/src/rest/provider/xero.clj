(ns rest.provider.xero
  (:require
   [jsonista.core :as j] ; json read/write
   [martian.core :as martian]
   [schema.core :as s]
   [martian.interceptors :as interceptors]
   [martian.clj-http :as martian-http]
   [rest.oauth2 :refer [martian-oauth2 add-authentication-header]]))

(defn parse-json [json]
  (j/read-value json j/keyword-keys-object-mapper))

(def endpoints
  [{:route-name :userinfo
    :summary "user info"
    :method :get
    :path-parts ["/api.xro/2.0/Organisation"]
    :produces ["application/json"]
    :consumes ["application/json"]}
   {:route-name :tenants
    :summary "list tenants"
    :method :get
    :path-parts ["/connections"]
    :produces ["application/json"]
    :consumes ["application/json"]}
   ; CONTACT / GROUP
   {:route-name :contact-list
    :summary "list contacts"
    :method :get
    :path-parts ["/api.xro/2.0/Contacts/"]
    :produces ["application/json"]
    :consumes ["application/json"]}
   {:route-name :contact
    :summary "get contact"
    :method :get
    :path-parts ["/api.xro/2.0/Contacts/" :contact-id]
    :path-schema {:contact-id s/Str}
    :produces ["application/json"]
    :consumes ["application/json"]}
   {:route-name :contact-create
    :summary "creates/updates contacts"
    :method :post
    :path-parts ["/api.xro/2.0/Contacts/"]
    :body-schema {:c s/Any}
    :produces ["application/json"]
    :consumes ["application/json"]}
   #_{:route-name :contact-update ; post works for update; put is not needed
      :summary "update contact"
      :method :put
      :path-parts ["/api.xro/2.0/Contacts/" :contact-id]
      :path-schema {:contact-id s/Str}
      :body-schema {:c {:Contacts s/Any}}
      :produces ["application/json"]
      :consumes ["application/json"]}
   {:route-name :add-contacts-to-group
    :summary "adds contacts to contact-group"
    :method :put
    :path-parts ["/api.xro/2.0/ContactGroups/" :group-id "/Contacts"]
    :path-schema {:group-id s/Str}
    :body-schema {:c {:Contacts s/Any}}
    :produces ["application/json"]
    :consumes ["application/json"]}
   {:route-name :contact-groups
    :summary "list contact-group"
    :method :get
    :path-parts ["/api.xro/2.0/ContactGroups"]
    :produces ["application/json"]
    :consumes ["application/json"]}
   {:route-name :contact-group
    :summary "get contact-group"
    :method :get
    :path-parts ["/api.xro/2.0/ContactGroups/" :group-id]
    :path-schema {:group-id s/Str}
      ;:body-schema {:c {:Contacts s/Any}}
    :produces ["application/json"]
    :consumes ["application/json"]}
   ;; INVOICE
   {:route-name :invoice
    :summary "get invoice"
    :method :get
    :path-parts ["/api.xro/2.0/Invoices/" :invoice-id]
    :path-schema {:invoice-id s/Str}
    :produces ["application/json"]
    :consumes ["application/json"]}
   {:route-name :invoice-list
    :summary "list invoices"
    :method :get
    :path-parts ["/api.xro/2.0/Invoices/"]
    :query-schema {s/Any s/Any}
    :produces ["application/json"]
    :consumes ["application/json"]}
   {:route-name :invoice-list-since
    :summary "list invoices modified-since"
    :method :get
    :path-parts ["/api.xro/2.0/Invoices/"]
    :query-schema {s/Any s/Any}
    :produces ["application/json"]
    :consumes ["application/json"]}
   {:route-name :branding-themes
    :summary "list branding-themes"
    :method :get
    :path-parts ["/api.xro/2.0/BrandingThemes"]
    :produces ["application/json"]
    :consumes ["application/json"]}
   {:route-name :invoice-create
    :summary "creates invoices"
    :method :post
    :path-parts ["/api.xro/2.0/Invoices/"]
   ;:path-schema {:contact-id s/Str}
    :body-schema {:c {:invoices s/Any}}
    :produces ["application/json"]
    :consumes ["application/json"]}
   ;; PRODUCT
   {:route-name :product-list
    :summary "list products"
    :method :get
    :path-parts ["/api.xro/2.0/Items/"]
    :query-schema {s/Any s/Any}
    :produces ["application/json"]
    :consumes ["application/json"]}
   ;; PO
   {:route-name :order-list
    :summary "list orders"
    :method :get
    :path-parts ["/api.xro/2.0/PurchaseOrders/"] ; https://api.xero.com/api.xro/2.0/
    :query-schema {s/Any s/Any}
    :produces ["application/json"]
    :consumes ["application/json"]}
   ;; REPORT
   {:route-name :report
    :summary "xero report"
    :method :get
    :path-parts ["/api.xro/2.0/Reports/" :report-name] ; https://api.xero.com/api.xro/2.0/BankSummary
    :path-schema {:report-name s/Str}
    :query-schema {s/Any s/Any}
    :produces ["application/json"]
    :consumes ["application/json"]}])

; :path-schema {:id s/Int}
; :query-schema {:q s/Str}

(defn martian-xero [this]
  (let [m (martian-oauth2 this
                          :xero
                          "https://api.xero.com"
                          endpoints)]
    m))

;; XERO-TENANT

(defn- add-tenant-header [tenant-id]
  {:name ::add-tenant-header
   :enter (fn [ctx]
            (assoc-in ctx
                      [:request :headers "Xero-Tenant-Id"]
                      tenant-id))})

(defn- interceptors-tenant [this tenant-id]
  (concat
   martian/default-interceptors
   [(add-authentication-header this :xero)
    (add-tenant-header tenant-id)
    interceptors/default-encode-body
    interceptors/default-coerce-response
    martian-http/perform-request]))

(defn martian-xero-tenant
  ([this tenant-id]
   (martian-xero-tenant this endpoints tenant-id))
  ([this endpoints tenant-id]
   (let [m (martian-http/bootstrap
            "https://api.xero.com"
            endpoints
            {:interceptors (interceptors-tenant this tenant-id)})]
     m)))

;; XERO-TENANT-SINCE

(defn- add-modified-since-header [dt]
  {:name ::add-modified-since-header
   :enter (fn [ctx]
            (assoc-in ctx
                      [:request :headers "If-Modified-Since"]
                      dt))})

(defn- interceptors-tenant-since [this tenant-id since]
  (concat
   (interceptors-tenant this tenant-id)
   [(add-modified-since-header since)]))

(defn martian-xero-tenant-since [this tenant-id since]
  (martian-http/bootstrap
   "https://api.xero.com"
   endpoints
   {:interceptors (interceptors-tenant-since this tenant-id since)}))


(defn xero-request
  ([m req-type req-opts]
   (xero-request m req-type req-opts nil))
  ([m req-type req-opts extract-type]
   (try
     (let [body (-> (martian/response-for m req-type req-opts)
                    :body)]
       (if extract-type
         (extract-type body)
         body))
     (catch Exception ex
       (let [data (ex-data ex)
             data-short (select-keys data [:reason-phrase :type
                                           :status :length
                                           :body])
             body (:body data)
             body (if (= (:status data) 400)
                    (try (parse-json body)
                         (catch Exception _jsex
                           body))
                    body)]
        ;(println "keys: " (keys data))
         (println "xero req " req-type req-opts " failed: " data-short)
         (if body
           (throw (ex-info (str "xero error for " req-type)
                           (if (map? body)
                             body
                             {:error body})))
           (throw ex)))))))
 
  

