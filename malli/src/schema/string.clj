(ns schema.string
  (:require [malli.core :as m]))

;; using a predicate:
(m/validate string? "hello")

; using :string Schema:
(m/validate :string "hello")
(m/validate [:string {:min 1, :max 4}] "")

; using regular expressions:
(m/validate #"a+b+c+" "abbccc")

;; :re with string
(m/validate [:re ".{3,5}"] "abc")

;; :re with regex
(m/validate [:re #".{3,5}"] "abc")

;; NB: re-find semantics
(m/validate [:re #"\d{4}"] "1234567")

;; anchor with ^...$ if you want to strictly match the whole string
(m/validate [:re #"^\d{4}$"] "1234567")