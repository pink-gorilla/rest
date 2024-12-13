(ns rest.provider.woo
  (:require
   [martian.clj-http :as martian-http]
   [martian.core :as martian]
   [martian.interceptors :as interceptors]
   [schema.core :as s]))

(def endpoints
  [{:route-name :orders
    :summary "loads orders"
    :method :get
    :path-parts ["/orders/"]
   ;:path-schema {:id s/Int}
    :produces ["application/json"]
    :consumes ["application/json"]}
   {:route-name :load-order
    :summary "loads one order"
    :method :get
    :path-parts ["/orders/" :id]
    :path-schema {:id s/Int}
    :produces ["application/json"]
    :consumes ["application/json"]}
   {:route-name :products
    :summary "loads products"
    :method :get
    :path-parts ["/products/"]
    :produces ["application/json"]
    :consumes ["application/json"]}])

(defn add-authentication-query-params [{:keys [consumer-key consumer-secret]}]
  {:name ::add-authentication-query-params
   :enter (fn [ctx]
            (-> ctx
                (assoc-in [:request :query-params :consumer_key] consumer-key)
                (assoc-in [:request :query-params :consumer_secret] consumer-secret)))})

(defn interceptors [this]
  {:interceptors
   (concat
    martian/default-interceptors
    [(add-authentication-query-params this)
     interceptors/default-encode-body
     interceptors/default-coerce-response
     martian-http/perform-request])})

(defn base-url [{:keys [shop-url]}]
  (str shop-url "/wp-json/wc/v3"))

(defn martian-woo 
  "this needs to be a map with :shop-url :consumer-key :consumer-secret"
  [this]
  (assert (map? this) "this needs to be a map")
  (assert (:show-url this) "the woo map needs to have :shop-url")
  (assert (:consumer-key this) "the woo map needs to have :consumer-key")
  (assert (:consumer-secret this) "the woo map needs to have :consumer-secret")
  (let [m (martian-http/bootstrap (base-url this) endpoints (interceptors this))]
    m))

