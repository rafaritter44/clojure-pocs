(ns syntax.map
  (:require [malli.core :as m]))

(def non-empty-string
  (m/from-ast {:type :string
               :properties {:min 1}}))

(comment
  (m/schema? non-empty-string)

  (m/validate non-empty-string "")
  (m/validate non-empty-string "hello")

  (m/ast non-empty-string)
  )