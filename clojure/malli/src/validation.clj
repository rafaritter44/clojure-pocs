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

;; maps are open by default
(m/validate
 [:map [:x :int]]
 {:x 1, :extra "key"})

;; maps can be closed with :closed property
(m/validate
 [:map {:closed true} [:x :int]]
 {:x 1, :extra "key"})

;; maps keys are not limited to keywords
(m/validate
 [:map
  ["status" [:enum "ok"]]
  [1 :any]
  [nil :any]
  [::a :string]]
 {"status" "ok"
  1 'number
  nil :yay
  ::a "properly awesome"})

;; most core-predicates are mapped to schemas
(m/validate string? "hello")