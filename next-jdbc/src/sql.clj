(ns sql
  (:require [core :refer [ds]]
            [next.jdbc.sql :as sql]))

(comment
  ;; sql/insert!
  (sql/insert! ds :address {:name "Carlo" :email "carlo@miracolieucaristici.org"})

  ;; sql/insert-multi!
  (sql/insert-multi! ds :address
    [:name :email]
    [["Alice" "alice@email.com"]
     ["Bob" "bob@email.com"]
     ["Charlie" "charlie@email.com"]])
  (sql/insert-multi! ds :address
    [{:name "David" :email "david@email.com"}
     {:name "Eve" :email "eve@email.com"}
     {:name "Frank" :email "frank@email.com"}])
  (sql/insert-multi! ds :address
    [:name :email]
    [["Grace" "grace@email.com"]
     ["Heidi" "heidi@email.com"]
     ["Ivan" "ivan@email.com"]]
    {:batch true})
  (sql/insert-multi! ds :address
    [{:name "Judy" :email "judy@email.com"}
     {:name "Mallory" :email "mallory@email.com"}
     {:name "Niaj" :email "niaj@email.com"}]
    {:batch true})

  ;; sql/query
  (sql/query ds ["select * from address where name = ?" "Carlo"])

  ;; sql/update!
  (sql/update! ds :address {:email "carlo@carloacutis.com"} {:name "Carlo"})
  (sql/update! ds :address {:email "acutis@carloacutis.com"} ["name = ?" "Carlo"])

  ;; sql/delete!
  (sql/delete! ds :address {:name "Niaj"})
  (sql/delete! ds :address ["name = ?" "Mallory"])

  ;; sql/find-by-keys
  (sql/find-by-keys ds :address {:name "Carlo" :email "carlo@carloacutis.com"})
  (sql/find-by-keys ds :address ["name = ? AND email = ?"
                                 "Carlo" "carlo@carloacutis.com"])
  (sql/find-by-keys ds :address ["email LIKE ?" "%@email.com"]
                                 {:order-by [[:id :asc]]})
  (sql/find-by-keys ds :address ["name IN (?, ?, ?)" "Alice" "Bob" "Charlie"])
  (sql/find-by-keys ds :address {:name "Carlo"} {:columns [[:email :email_alias]]})
  (sql/find-by-keys ds :address {:name "Carlo"} {:columns [["count(*)" :n]]})
  (sql/find-by-keys ds :address :all {:order-by [:id] :offset 5 :fetch 10})
  )