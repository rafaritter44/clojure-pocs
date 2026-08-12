(ns syntax.vector
  (:require [malli.core :as m]))

(def non-empty-string
  (m/schema [:string {:min 1}]))

(comment
  (m/schema? non-empty-string)

  (m/validate non-empty-string "")
  (m/validate non-empty-string "hello")

  (m/form non-empty-string)
  )