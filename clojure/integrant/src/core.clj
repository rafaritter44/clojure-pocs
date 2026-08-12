(ns core
  (:require [integrant.core :as ig]))

;; ig/ref
(comment
  (def config
    {:adapter/jetty {:port    8080
                     :handler (ig/ref :handler/greet)}
     :handler/greet {:name "Alice"}})
  )

;; ig/read-string
(def config
  (ig/read-string (slurp "config.edn")))

;; ig/init-key
(defmethod ig/init-key :adapter/jetty
  [_ {:keys [port handler]}]
  (println "Starting Jetty on port" port)
  {:port port, :handler handler})
(defmethod ig/init-key :handler/greet
  [_ {:keys [name]}]
  (println "Creating greet handler for" name)
  #(str "Hello, " name "!"))

;; ig/halt-key!
(defmethod ig/halt-key! :adapter/jetty
  [_ server]
  (println "Stopping Jetty on port" (:port server)))

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
  )

;; omitting defmethod
(def config-2 {::sugared-greet {:name "Bob"}})
(defn sugared-greet [{:keys [name]}]
  #(str "Hi, " name "!"))
(def system-2 (ig/init config-2))
(comment
  ((::sugared-greet system-2))
  )