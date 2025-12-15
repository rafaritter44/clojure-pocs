(ns cons)

(defn cons [a b]
  (fn [pick]
    (cond (= pick 1) a
          (= pick 2) b)))
(defn car [x]
  (x 1))
(defn cdr [x]
  (x 2))

(defn cons [x y]
  (fn [m] (m x y)))
(defn car [x]
  (x (fn [a d] a)))
(defn cdr [x]
  (x (fn [a d] d)))

(comment
  (def a (cons 37 49))
  (car a)
  (cdr a)
  )