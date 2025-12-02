(ns map
  (:require [cons :refer [cons car cdr]]
            [for-each :refer [for-each one-to-four]]))

(defn map [p l]
  (if (nil? l)
    nil
    (cons (p (car l))
          (map p (cdr l)))))

(defn scale-list [s l]
  (map #(* s %)
       l))

(comment
  (for-each println (map #(* 10 %) one-to-four))
  (for-each println (scale-list 10 one-to-four))
  )