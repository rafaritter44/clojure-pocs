(ns atom)

;; atom
(def counter (atom 0))

(comment
  ;; deref
  (deref counter)
  @counter

  ;; swap!
  (swap! counter inc)
  (swap-vals! counter inc)

  ;; reset!
  (reset! counter 0)
  (reset-vals! counter 0)

  ;; compare-and-set!
  (compare-and-set! counter 0 1)

  ;; set-validator!
  (set-validator! counter (every-pred number? (complement neg?)))
  (reset! counter -1)

  ;; add-watch
  (add-watch counter :watch
             (fn [key atom old-state new-state]
               (println "Counter changed from" old-state "to" new-state)))
  (remove-watch counter :watch))