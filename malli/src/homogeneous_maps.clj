(ns homogeneous-maps
  (:require [malli.core :as m]))

(m/validate
 [:map-of :string [:map [:lat number?] [:long number?]]]
 {"oslo" {:lat 60 :long 11}
  "helsinki" {:lat 60 :long 24}})