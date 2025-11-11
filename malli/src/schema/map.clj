(ns schema.map
  (:require [malli.core :as m]))

;; homogeneous map
(m/validate
 [:map-of :string [:map [:lat number?] [:long number?]]]
 {"oslo"     {:lat  60
              :long 11}
  "helsinki" {:lat  60
              :long 24}})

;; map with default schema
(m/validate
 [:map
  [:x :int]
  [:y :int]
  [::m/default [:map-of :int :int]]]
 {:x 1
  :y 2
  1  1
  2  2})

;; default branching can be arbitrarily nested
(m/validate
 [:map
  [:x :int]
  [::m/default [:map
                [:y :int]
                [::m/default [:map-of :int :int]]]]]
 {:x 1
  :y 2
  1  1
  2  2})