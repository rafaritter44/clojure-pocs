(ns ref)

(def account-validator (every-pred number? (complement neg?)))

(defn ->accounts []
  {:checking (ref 100M :validator account-validator)
   :savings  (ref 0M :validator account-validator)})

(defn balances [a]
  (update-vals a deref))

(defn transfer [from from-type to to-type amount]
  (dosync
   (alter (from-type from) - amount)
   (alter (to-type to) + amount)
   (when (< (+ @(:checking from) @(:savings from)) 50M)
     (throw (Exception. "Combined balance too low")))
   {:from (balances from)
    :to   (balances to)}))

(def a1 (->accounts))
(def a2 (->accounts))

(comment
  (balances a1)
  (balances a2)
  (transfer a1 :checking a2 :savings 10M)
  (transfer a2 :savings a1 :checking 20M)
  )