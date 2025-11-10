(ns validation
  (:require [malli.core :as m]))

;; with schema instances
(m/validate (m/schema :int) 1)

;; with vector syntax
(m/validate :int 1)
(m/validate :int "1")
(m/validate [:= 1] 1)
(m/validate [:enum 1 2] 1)
(m/validate [:and :int [:> 6]] 7)
(m/validate [:qualified-keyword {:namespace :aaa}] :aaa/bbb)

;; optimized (pure) validation function for best performance
(def valid?
  (m/validator
    [:map
     [:x :boolean]
     [:y {:optional true} :int]
     [:z :string]]))

(valid? {:x true, :z "hello"})