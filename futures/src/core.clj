(ns core)

;; future
(def f (future
         (Thread/sleep 10000)
         "done"))

(comment
  ;; deref
  (deref f)
  @f

  ;; predicates
  (future? f)
  (future-done? f)
  (future-cancelled? f)
  (realized? f)

  ;; future-cancel
  (future-cancel f)
  )