(ns rest.paging
  (:require
   [clojure.pprint :refer [print-table]]
   [modular.oauth2.token.refresh :refer [refresh-access-token]]
 
   [modular.rest.martian.xero :refer [martian-xero martian-xero-tenant]]
   ))

(defn paging-test []
  (let [items 12 
        pgsize 5
        src (vec (repeatedly items #(java.util.UUID/randomUUID)))
        api (fn [tok]
              (let [tok (or tok 0)]
                (when (< tok items)
                  {:tok (+ tok pgsize)
                   :ret (subvec src tok (min (+ tok pgsize) items))})))
        results  (mapcat identity (iteration api :kf :tok :vf :ret))
                   ]
    ;(is (= src
    ;     (mapcat identity (iteration api :kf :tok :vf :ret))
    ;     (into [] cat (iteration api :kf :tok :vf :ret)))
    ;     )

    (info "iterated results:" (pr-str results)) 
;
    ))

(defn print-invoices [invoices]
  (->> invoices
      (map #(select-keys % [:Type :Status :InvoiceNumber  :DateString :Name :Total
            ;  :DueDateString :AmountDue
             ;:InvoiceID :UpdatedDateUTC 
            ]))
       (print-table)))


