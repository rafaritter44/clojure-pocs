(ns core
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

;; jdbc/get-datasource
(def db {:dbtype "h2"
         :dbname "example"})
(defonce ds (jdbc/get-datasource db))

(comment
  ;; jdbc/execute!
  (jdbc/execute! ds ["
    create table address (
      id int auto_increment primary key,
      name varchar(32),
      email varchar(255)
    )"])
  (jdbc/execute! ds ["
    insert into address (name, email)
      values ('Rafael', 'rafael@email.com')
    "])
  (jdbc/execute! ds ["select * from address"])

  ;; jdbc/execute-one!
  (jdbc/execute-one! ds ["
    insert into address (name, email)
      values ('Someone Else', 'some@elsewhere.com')
    "] {:return-keys true})
  (jdbc/execute-one! ds ["select * from address where id = ?" 2])

  ;; :builder-fn
  (jdbc/execute-one! ds ["
    insert into address (name, email)
      values ('Someone Else', 'some@elsewhere.com')
    "] {:return-keys true :builder-fn rs/as-unqualified-lower-maps})
  (jdbc/execute-one! ds ["select * from address where id = ?" 3]
                     {:builder-fn rs/as-unqualified-lower-maps})

  ;; jdbc/with-options
  (def ds-opts (jdbc/with-options ds {:builder-fn rs/as-unqualified-lower-maps}))
  (jdbc/execute-one! ds-opts ["
    insert into address (name, email)
      values ('Someone Else', 'some@elsewhere.com')
    "] {:return-keys true})
  (jdbc/execute-one! ds-opts ["select * from address where id = ?" 4])

  ;; jdbc/plan
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
  (reduce
   (fn [cost row]
     (+ cost (* (:unit_price row)
                (:unit_count row))))
   0
   (jdbc/plan ds ["select * from invoice where customer_id = ?" 100]))
  )