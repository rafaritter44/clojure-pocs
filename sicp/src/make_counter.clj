(ns make-counter)

(defn make-counter [n]
  (let [n (atom n)]
    (fn []
      (swap! n inc)
      @n)))

(def c1 (make-counter 0))
(def c2 (make-counter 10))

(comment
  (c1)
  (c2)
  )