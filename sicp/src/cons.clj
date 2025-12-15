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

(defn cons [x y]
  (let [x (atom x)
        y (atom y)]
    (fn [m]
      (m @x
         @y
         (fn [n] (reset! x n))
         (fn [n] (reset! y n))))))
(defn car [x]
  (x (fn [a d sa sd] a)))
(defn cdr [x]
  (x (fn [a d sa sd] d)))
(defn set-car! [x y]
  (x (fn [a d sa sd] (sa y))))
(defn set-cdr! [x y]
  (x (fn [a d sa sd] (sd y))))

(def caar (comp car car))
(def cadr (comp cdr car))
(def cdar (comp car cdr))
(def cddr (comp cdr cdr))

(comment
  (def a (cons 1 2))
  (def b (cons a a))
  (car a)
  (cdr a)
  (set-car! a 3)
  (set-cdr! (car b) 4)
  (set-cdr! b (cons 5 6))
  (caar b)
  (cadr b)
  (cdar b)
  (cddr b)
  )