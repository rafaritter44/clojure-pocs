(ns agent)

;; agent
(defn ->agent [error-mode]
  (agent 0
         :validator (every-pred number? (complement neg?))
         :error-handler (fn [a e] (println {:agent @a :error (.getMessage e)}))
         :error-mode error-mode))
(def a1 (->agent :continue))
(def a2 (->agent :fail))

(comment
  ;; deref
  (deref a1)
  @a2

  ;; send
  (send a1 #(nth (iterate inc %) 1000000000))
  (send-off a2 #(do (Thread/sleep 10000)
                    (inc %)))

  ;; await
  (await a1 a2)
  (await-for 10000 a1 a2)

  ;; add-watch
  (letfn [(watch [key agent old-state new-state]
                 (println "Counter changed from" old-state "to" new-state))]
    (add-watch a1 :watch watch)
    (add-watch a2 :watch watch))
  (remove-watch a1 :watch)
  (remove-watch a2 :watch)

  ;; restart-agent
  (restart-agent a1 0)
  (restart-agent a2 0)

  ;; shutdown-agents
  (shutdown-agents)

  ;; *agent*
  *agent*

  ;; release-pending-sends
  (release-pending-sends)
  )