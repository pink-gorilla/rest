(ns rest.provider.github
  (:require
   [schema.core :as s]
   [rest.oauth2 :refer [martian-oauth2]]))

(def endpoints
  [{:route-name :userinfo
    :summary "user info"
    :method :get
    :path-parts ["/user"]
    :produces ["application/json"]
    :consumes ["application/json"]}
   {:route-name :search-repo
    :summary "search repositories"
    :method :get
    :path-parts ["/search/repositories"]
   ;:path-schema {:id s/Int}
    :query-schema {:q s/Str}
    :produces ["application/json"]
    :consumes ["application/json"]}])

(defn martian-github [this]
  (let [m (martian-oauth2
           this
           :github
           "https://api.github.com"
           endpoints)]
    m))

