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
  )