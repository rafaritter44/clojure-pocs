(ns transaction
  (:require [core :refer [ds]]
            [next.jdbc :as jdbc]))

(comment
  ;; jdbc/with-transaction
  (jdbc/with-transaction [tx ds]
    (jdbc/execute! tx ["insert into address (name, email) values ('A', 'a@email.com')"])
    (when (rand-nth [true false])
      (throw (Exception.)))
    (jdbc/execute! tx ["insert into address (name, email) values ('B', 'b@email.com')"]))

  (jdbc/execute! ds ["select * from address where name in ('A', 'B')"])
  )