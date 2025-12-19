(ns ref)

(def account-validator (every-pred number? (complement neg?)))

(defn ->account []
  {:checking                 (ref 100M :validator account-validator)
   :savings                  (ref 0M :validator account-validator)
   :transfers-sent-today     (ref 0)
   :transfers-received-today (ref 0)})

(defn balance [a]
  (update-vals a deref))

(defn transfer [from from-type to to-type amount]
  (dosync
   (alter (from-type from) - amount)
   (when (< (+ @(:checking from) @(:savings from)) 50M)
     (throw (Exception. "Combined balance too low")))
   (alter (to-type to) + amount)
   (commute (:transfers-sent-today from) inc)
   (commute (:transfers-received-today to) inc)
   {:from (balance from)
    :to   (balance to)}))

(defn reset-transfers-count [a]
  (dosync
   (ref-set (:transfers-sent-today a) 0)
   (ref-set (:transfers-received-today a) 0)))

(def a1 (->account))
(def a2 (->account))

(comment
  (balance a1)
  (balance a2)
  (transfer a1 :checking a2 :savings 10M)
  (transfer a2 :savings a1 :checking 20M)
  (reset-transfers-count a1)
  (reset-transfers-count a2)
  )