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
  )
