(ns schema.sequence
  (:require [malli.core :as m])
  (:require [clojure.spec.alpha :as s])
  (:require [criterium.core :as cc]))

;; you can use :sequential to describe homogeneous sequential Clojure collections
(m/validate [:sequential :any] (list "this" 'is :number 7))
(m/validate [:sequential :int] [7 13])
(m/validate [:sequential :int] #{7 13})

;; Malli also supports sequence regexes (also called sequence expressions) like Seqexp and Spec
;; the supported operators are :cat & :catn for concatenation / sequencing
(m/validate [:cat :string :int] ["foo" 0])
(m/validate [:catn [:s :string] [:n :int]] ["foo" 0])

;; :alt & :altn for alternatives
(m/validate [:alt :keyword :string] ["foo"])
(m/validate [:altn [:kw :keyword] [:s :string]] ["foo"])

;; and :?, :*, :+ & :repeat for repetition:
(m/validate [:? :int] [])
(m/validate [:? :int] [1])
(m/validate [:? :int] [1 2])
(m/validate [:* :int] [])
(m/validate [:* :int] [1 2 3])
(m/validate [:+ :int] [])
(m/validate [:+ :int] [1])
(m/validate [:+ :int] [1 2 3])
(m/validate [:repeat {:min 2, :max 4} :int] [1])
(m/validate [:repeat {:min 2, :max 4} :int] [1 2])
(m/validate [:repeat {:min 2, :max 4} :int] [1 2 3 4])
(m/validate [:repeat {:min 2, :max 4} :int] [1 2 3 4 5])

;; :catn and :altn allow naming the subsequences / alternatives
(m/explain
 [:* [:catn [:prop :string] [:val [:altn [:s :string] [:b :boolean]]]]]
 ["-server" "foo" "-verbose" 11 "-user" "joe"])

;; while :cat and :alt just use numeric indices for paths:
(m/explain
 [:* [:cat :string [:alt :string :boolean]]]
 ["-server" "foo" "-verbose" 11 "-user" "joe"])

;; as all these examples show, the sequence expression (seqex) operators take any non-seqex child schema to mean a sequence of one element that matches that schema
;; to force that behaviour for a seqex child :schema can be used:
(m/validate
 [:cat [:= :names] [:schema [:* :string]] [:= :nums] [:schema [:* number?]]]
 [:names ["a" "b"] :nums [1 2 3]])

;; whereas
(m/validate
 [:cat [:= :names] [:* :string] [:= :nums] [:* number?]]
 [:names "a" "b" :nums 1 2 3])

(comment
  ;; although a lot of effort has gone into making the seqex implementation fast
  (let [valid? (partial s/valid? (s/* int?))]
    (cc/quick-bench (valid? (range 10))))
  (let [valid? (m/validator [:* int?])]
    (cc/quick-bench (valid? (range 10))))

  ;; it is always better to use less general tools whenever possible:
  (let [valid? (partial s/valid? (s/coll-of int?))]
    (cc/quick-bench (valid? (range 10))))
  (let [valid? (m/validator [:sequential int?])]
    (cc/quick-bench (valid? (range 10))))
  )