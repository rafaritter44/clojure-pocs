(ns properties
  (:require [malli.core :as m]))

(def Age
  [:and
   {:title "Age"
    :description "It's an age"
    :json-schema/example 20}
   :int [:> 18]])

(comment
  (m/properties Age)
  )