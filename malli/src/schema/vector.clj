(ns schema.vector
  (:require [malli.core :as m]))

;; you can use :vector to describe homogeneous Clojure vectors
(m/validate [:vector :int] [1 2 3])
(m/validate [:vector :int] (list 1 2 3))

;; a :tuple schema describes a fixed length Clojure vector of heterogeneous elements:
(m/validate [:tuple :keyword :string number?] [:bing "bang" 42])

;; to create a vector schema based on a seqex, use :and
;; non-empty vector starting with a keyword
(m/validate [:and [:cat :keyword [:* :any]]
                  vector?]
            [:a 1])
(m/validate [:and [:cat :keyword [:* :any]]
                  vector?]
            (:a 1))