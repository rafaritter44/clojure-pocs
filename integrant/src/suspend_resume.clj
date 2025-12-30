(ns suspend-resume
  (:require [integrant.core :as ig]))

;; ig/read-string
(def config
  (ig/read-string (slurp "config.edn")))

;; ig/init-key
(defmethod ig/init-key :adapter/jetty
  [_ {:keys [port handler]}]
  (println "Starting Jetty on port" port)
  (let [handler-atom (atom (delay handler))]
    {:port         port
     :handler      #(@@handler-atom)
     :handler-atom handler-atom}))
(defmethod ig/init-key :handler/greet
  [_ {:keys [name]}]
  (println "Creating greet handler for" name)
  #(str "Hello, " name "!"))

;; ig/halt-key!
(defmethod ig/halt-key! :adapter/jetty
  [_ server]
  (println "Stopping Jetty on port" (:port server)))

;; ig/suspend-key!
(defmethod ig/suspend-key! :adapter/jetty
  [_ {:keys [port handler-atom]}]
  (println "Suspending Jetty on port" port)
  (reset! handler-atom (promise)))

;; ig/resume-key
(defmethod ig/resume-key :adapter/jetty
  [key opts old-opts old-impl]
  (if (= (dissoc opts :handler)
         (dissoc old-opts :handler))
    (do (println "Options haven't changed. Delivering new handler to promise...")
        (deliver @(:handler-atom old-impl) (:handler opts))
        old-impl)
    (do (println "Options have changed. Halting and re-initializing...")
        (ig/halt-key! key old-impl)
        (ig/init-key key opts))))

;; ig/init
(def system (ig/init config))

(comment
  ;; system usage
  (let [handler (get-in system [:adapter/jetty :handler])]
    (handler))
  (let [handler (:handler/greet system)]
    (handler))

  ;; ig/halt!
  (ig/halt! system)

  ;; ig/suspend!
  (ig/suspend! system)

  ;; ig/resume
  (let [new-config (assoc-in config [:handler/greet :name] "Bob")
        new-system (ig/resume new-config system)
        handler    (get-in new-system [:adapter/jetty :handler])]
    (println (handler))
    (ig/halt! new-system))
  (let [new-config (-> config
                       (assoc-in [:handler/greet :name] "Charlie")
                       (assoc-in [:adapter/jetty :port] 80))
        new-system (ig/resume new-config system)
        handler    (get-in new-system [:adapter/jetty :handler])]
    (println (handler))
    (ig/halt! new-system))
  )