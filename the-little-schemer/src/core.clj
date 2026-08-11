(ns core)

(comment
  ;; Recursive length without define

  ;; First version
  (((fn [mk-length]
      (mk-length mk-length))
    (fn [mk-length]
      (fn [l]
        (if (empty? l)
          0
          (inc ((mk-length mk-length)
                (rest l)))))))
   '('apples))
  )
