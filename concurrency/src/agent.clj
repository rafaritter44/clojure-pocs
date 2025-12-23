(ns agent)

;; agent
(defn ->agent [error-mode]
  (agent 0
         :validator (every-pred number? (complement neg?))
         :error-handler (fn [a e] (println {:agent @a :error (.getMessage e)}))
         :error-mode error-mode))
(def a1 (->agent :continue))
(def a2 (->agent :fail))
(def a3 (->agent :continue))
(def a4 (->agent :fail))
(def agents [a1 a2 a3 a4])

(comment
  ;; add-watch
  (letfn [(watch [key agent old-state new-state]
            (println "Counter changed from" old-state "to" new-state))]
    (doseq [a agents]
      (add-watch a :watch watch)))
  (doseq [a agents]
    (remove-watch a :watch))

  ;; deref
  (map deref agents)

  ;; send
  (send a1 #(nth (iterate inc %) 1000000000))
  (letfn [(action [state]
            (println "Agent" *agent*)
            (Thread/sleep 10000)
            (inc state))]
    (send-off a2 #(do (send-off a3 action)
                      (send-off a4 action)
                      (println "Released" (release-pending-sends))
                      (action %))))

  ;; await
  (apply await agents)
  (apply await-for 10000 agents)

  ;; restart-agent
  (restart-agent a1 0)
  (restart-agent a2 0)

  ;; shutdown-agents
  (shutdown-agents)
  )