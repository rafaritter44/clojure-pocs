(ns core)

(comment
  ;; Recursive length without define

  ;; First version
  (((fn [mk-length]
      (mk-length mk-length))
    (fn [mk-length]
      (fn [l]
        (cond
          (empty? l) 0
          :else (inc
                 ((mk-length mk-length)
                  (rest l)))))))
   '('apples))

  ;; Second version
  (((fn [le]
      ((fn [mk-length]
         (mk-length mk-length))
       (fn [mk-length]
         (le (fn [x]
               ((mk-length mk-length) x))))))
    (fn [length]
      (fn [l]
        (cond
          (empty? l) 0
          :else (inc (length (rest l)))))))
   '('apples))
  )

(defn Y
  "The applicative-order Y combinator"
  [le]
  ((fn [f] (f f))
   (fn [f]
     (le (fn [x] ((f f) x))))))

(comment
  ;; Third version
  ((Y
    (fn [length]
      (fn [l]
        (cond
          (empty? l) 0
          :else (inc (length (rest l)))))))
   '('apples))
  )
