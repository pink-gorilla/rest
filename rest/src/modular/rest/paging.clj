(ns modular.rest.paging
  (:require
   [martian.core :as martian]))


(defn make-paginate [m handler params result-kw]
  (fn [page]
    (let [page (or page 1)
           ;_ (info "requesting page: " page)
          params (assoc params :page page)
          result (->> (martian/response-for m handler params)
                      :body
                      result-kw)
          result? (when (> (count result) 0) true)]
       ;(info "page size: " (count result))
      (when result?
        {:page (inc page)
         :result result}))))

(defn request-paginated [m handler params result-kw]
  (let [api (make-paginate m handler params result-kw)]
    (mapcat identity (iteration api :kf :page :vf :result))))