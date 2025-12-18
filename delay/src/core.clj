(ns core)

;; delay
(def d (delay
         (println "computing...")
         "done"))

(comment
  ;; deref
  (deref d)
  @d

  ;; force
  (force d)
  (force "works on anything")

  ;; delay?
  (delay? d)
  (delay? "not a delay")
  )