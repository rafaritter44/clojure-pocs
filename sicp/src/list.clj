(ns list
  (:require [cons :refer [cons]]
            [for-each :refer [for-each]]))

(comment
  (defn list [& items]
    items)
  )

(defn list [& items]
  (if (empty? items)
    nil
    (cons (first items)
          (apply list (rest items)))))

(def one-to-four
  (list 1 2 3 4))

(comment
  (for-each println one-to-four)
  )