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
  )