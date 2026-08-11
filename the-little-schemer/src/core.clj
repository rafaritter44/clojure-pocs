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
