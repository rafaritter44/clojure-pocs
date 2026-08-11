(ns core)

(comment
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
