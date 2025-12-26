(ns core
  (:require [com.stuartsierra.component :as component]))

(def connect-to-database (constantly "Connection"))

(defrecord Database [host port connection]
  component/Lifecycle
  (start [component]
    (println "Starting database...")
    (let [conn (connect-to-database host port)]
      (assoc component :connection conn)))
  (stop [component]
    (println "Stopping database...")
    (.close connection)
    ;; If you dissoc one of the base fields from a record, you get a plain map.
    (assoc component :connection nil)))

(defn new-database [host port]
  (map->Database {:host host :port port}))

(def execute-query (constantly {:username "Rafael" :email "rafael@ritter.com"}))
(def execute-insert (constantly true))

(defn get-user [database username]
  (execute-query (:connection database)
                 "SELECT * FROM users WHERE username = ?"
                 username))
(defn add-user [database username email]
  (execute-insert (:connection database)
                  "INSERT INTO users (username, email)"
                  username email))

(defrecord ExampleComponent [options cache database scheduler]
  component/Lifecycle
  (start [this]
    (println "Starting example component...")
    (assoc this :admin (get-user database "admin")))
  (stop [this]
    (println "Stopping example component...")
    this))

(defn example-component [config-options]
  (map->ExampleComponent {:options config-options
                          :cache (atom {})}))

(def new-scheduler (constantly "Scheduler"))

(comment
  (defn example-system [config-options]
    (let [{:keys [host port]} config-options]
      (component/system-map
       :db (new-database host port)
       :scheduler (new-scheduler)
       :app (component/using
             (example-component config-options)
             {:database  :db
              :scheduler :scheduler}))))

  (defn example-system [config-options]
    (let [{:keys [host port]} config-options]
      (component/system-map
       :database (new-database host port)
       :scheduler (new-scheduler)
       :app (component/using
             (example-component config-options)
             [:database :scheduler]))))
  )

(defn example-system [config-options]
  (let [{:keys [host port]} config-options]
    (-> (component/system-map
         :database (new-database host port)
         :scheduler (new-scheduler)
         :app (example-component config-options))
        (component/system-using
         {:app [:database :scheduler]}))))

(def system (example-system {:host "dbhost.com" :port 5432}))

(comment
  system
  (alter-var-root #'system component/start)
  (alter-var-root #'system component/stop)
  )