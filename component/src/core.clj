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