(ns profile
  (:require [integrant.core :as ig]))

(def config
  (-> "profile-config.edn"
      slurp
      ig/read-string
      (ig/deprofile [:dev])))

(defmethod ig/init-key :adapter/jetty
  [_ config]
  config)

(def system (ig/init config))

(comment
  system
  )