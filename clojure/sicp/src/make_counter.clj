(ns make-counter)

(defn make-counter [n]
  (let [n (atom n)]
    (fn []
      (reset! n (inc @n))
      @n)))

(defn make-counter [n]
  (let [n (atom n)]
    #(swap! n inc)))

(def c1 (make-counter 0))
(def c2 (make-counter 10))

(comment
  (c1)
  (c2)
  )