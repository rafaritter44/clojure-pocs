(ns select
  (:require [core :refer [ds]]
            [next.jdbc.plan :as plan]))

(comment
  ;; plan/select-one! (vector)
  (plan/select-one!
   ds [:n] ["select count(*) as n from invoice where customer_id = ?" 100])

  ;; plan/select-one! (function)
  (plan/select-one!
   ds :n ["select count(*) as n from invoice where customer_id = ?" 100])

  ;; plan/select! (into)
  (plan/select! ds
                :product
                ["select * from invoice where customer_id = ?" 100]
                {:into #{}})

  ;; plan/select! (simple keywords)
  (plan/select! ds
                [:id :product :unit_price :unit_count :customer_id]
                ["select * from invoice where customer_id = ?" 100])

  ;; plan/select! (qualified keywords)
  (plan/select! ds
                [:invoice/id :invoice/product
                 :invoice/unit_price :invoice/unit_count
                 :invoice/customer_id]
                ["select * from invoice where customer_id = ?" 100])

  ;; plan/select! (qualified keywords, ignoring the table name)
  (plan/select! ds
                [:foo/id :bar/product
                 :quux/unit_price :wibble/unit_count
                 :blah/customer_id]
                ["select * from invoice where customer_id = ?" 100])
  )