(ns core)

;; promise
(def p (promise))

(comment
  ;; deref
  (when (realized? p)
    (deref p))

  ;; deliver
  (deliver p 7)
  )