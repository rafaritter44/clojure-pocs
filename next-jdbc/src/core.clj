(ns core
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(def db {:dbtype "h2"
         :dbname "example"})

(defonce ds (jdbc/get-datasource db))

(comment
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

  (jdbc/execute-one! ds ["
    insert into address (name, email)
      values ('Someone Else', 'some@elsewhere.com')
    "] {:return-keys true})
  (jdbc/execute-one! ds ["select * from address where id = ?" 2])

  (jdbc/execute-one! ds ["
    insert into address (name, email)
      values ('Someone Else', 'some@elsewhere.com')
    "] {:return-keys true :builder-fn rs/as-unqualified-lower-maps})
  (jdbc/execute-one! ds ["select * from address where id = ?" 3]
                     {:builder-fn rs/as-unqualified-lower-maps})

  (def ds-opts (jdbc/with-options ds {:builder-fn rs/as-unqualified-lower-maps}))
  (jdbc/execute-one! ds-opts ["
    insert into address (name, email)
      values ('Someone Else', 'some@elsewhere.com')
    "] {:return-keys true})
  (jdbc/execute-one! ds-opts ["select * from address where id = ?" 4])
  )