(ns connection-pool
  (:require [core :refer [db]]
            [next.jdbc :as jdbc]
            [next.jdbc.connection :as connection])
  (:import [com.zaxxer.hikari HikariDataSource]))

(comment
  (with-open [^HikariDataSource ds (connection/->pool HikariDataSource db)]
    ;; This code initializes the pool and performs a validation check:
    (.close (jdbc/get-connection ds))
    ;; otherwise that validation check is deferred until the first connection
    ;; is requested in a regular operation:
    (jdbc/execute! ds ["select 1"]))
  )