(ns schema.seqable
  (:require [malli.core :as m]))

;; :seqable and :every validate identically with small, counted, or indexed collections
(m/validate [:seqable :int] [1 2 3])
(m/validate [:seqable :int] #{1 2 3})
(m/validate [:seqable :int] (sorted-set 1 2 3))
(m/validate [:seqable :int] (range 1000))
(m/validate [:seqable :int] (conj (vec (range 1000)) nil))
(m/validate [:every :int] [1 2 3])
(m/validate [:every :int] #{1 2 3})
(m/validate [:every :int] (sorted-set 1 2 3))
(m/validate [:every :int] (vec (range 1000)))
(m/validate [:every :int] (conj (vec (range 1000)) nil))

;; for large uncounted and unindexed collections, :every only checks a certain length
(m/validate [:seqable :int] (concat (range 1000) [nil]))
(m/validate [:every :int] (concat (range 1000) [nil]))