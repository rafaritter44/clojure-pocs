(ns registry
  (:require [malli.core :as m]))

;; qualified keys in a map
(m/validate
 [:map {:registry {::id :int
                   ::country :string}}
  ::id
  [:name :string]
  [::country {:optional true}]]
 {::id 1
  :name "Rafael"})