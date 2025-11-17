(ns schema.fn
  (:require [malli.core :as m]))

;; :fn allows any predicate function to be used:
(def my-schema
  [:and
   [:map
    [:x :int]
    [:y :int]]
   [:fn (fn [{:keys [x y]}] (> x y))]])

(m/validate my-schema {:x 1, :y 0})
(m/validate my-schema {:x 1, :y 2})