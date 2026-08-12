(ns schema.maybe
  (:require [malli.core :as m]))

;; use :maybe to express that an element should match some schema OR be nil:
(m/validate [:maybe :string] "bingo")
(m/validate [:maybe :string] nil)
(m/validate [:maybe :string] :bingo)