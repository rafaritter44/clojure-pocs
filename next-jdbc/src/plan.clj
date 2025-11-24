(ns plan
  (:require [core :refer [ds]]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(comment
  ;; setup
  (jdbc/execute-one! ds ["
    create table invoice (
      id int auto_increment primary key,
      product varchar(32),
      unit_price decimal(10, 2),
      unit_count int,
      customer_id int
    )"])
  (jdbc/execute-one! ds ["
    insert into invoice (product, unit_price, unit_count, customer_id)
    values ('apple', 0.99, 6, 100),
           ('banana', 1.25, 3, 100),
           ('cucumber', 2.49, 2, 100)
    "])

  ;; reduce
  (reduce
   (fn [cost row]
     (+ cost (* (:unit_price row)
                (:unit_count row))))
   0
   (jdbc/plan ds ["select * from invoice where customer_id = ?" 100]))

  ;; transduce
  (transduce
   (map #(* (:unit_price %) (:unit_count %)))
   +
   0
   (jdbc/plan ds ["select * from invoice where customer_id = ?" 100]))
  (transduce
   (map :unit_count)
   +
   0
   (jdbc/plan ds ["select * from invoice where customer_id = ?" 100]))

  ;; comp
  (transduce
   (comp (map (juxt :unit_price :unit_count))
         (map #(apply * %)))
   +
   0
   (jdbc/plan ds ["select * from invoice where customer_id = ?" 100]))

  ;; into
  (into #{}
        (map :product)
        (jdbc/plan ds ["select * from invoice where customer_id = ?" 100]))

  ;; run!
  (run! #(println (:product %))
        (jdbc/plan ds ["select * from invoice where customer_id = ?" 100]))

  ;; select-keys (simple keywords)
  (into []
        (map #(select-keys % [:id :product :unit_price :unit_count :customer_id]))
        (jdbc/plan ds ["select * from invoice where customer_id = ?" 100]))

  ;; select-keys (qualified keywords)
  (into []
        (map #(select-keys % [:invoice/id :invoice/product
                              :invoice/unit_price :invoice/unit_count
                              :invoice/customer_id]))
        (jdbc/plan ds ["select * from invoice where customer_id = ?" 100]))

  ;; select-keys (qualified keywords, ignoring the table name)
  (into []
        (map #(select-keys % [:foo/id :bar/product
                              :quux/unit_price :wibble/unit_count
                              :blah/customer_id]))
        (jdbc/plan ds ["select * from invoice where customer_id = ?" 100]))

  ;; do not do this:
  (into []
        (map #(into {} %))
        (jdbc/plan ds ["select * from invoice where customer_id = ?" 100]))

  ;; rs/datafiable-row
  (into []
        (map #(rs/datafiable-row % ds {}))
        (jdbc/plan ds ["select * from invoice where customer_id = ?" 100]))
  )