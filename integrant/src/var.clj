(ns var
  (:require [integrant.core :as ig]))

(def config
  (-> "var-config.edn"
      slurp
      ig/read-string
      (ig/bind {'port 8080})))

(defmethod ig/init-key :adapter/jetty
  [_ config]
  config)

(def system (ig/init config))

(comment
  system
  )