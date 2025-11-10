(ns core
  (:require [malli.core :as m])
  (:require [malli.generator :as mg]))

(def UserId :string)

(def Address
  [:map
   [:street :string]
   [:country [:enum "FI" "UA"]]])

(def User
  [:map
   [:id #'UserId]
   [:address #'Address]
   [:friends [:set {:gen/max 2} [:ref #'User]]]])

(comment
  (mg/generate User)
  (m/validate User *1)
  )