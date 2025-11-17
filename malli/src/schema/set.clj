(ns schema.set
  (:require [malli.core :as m]))

(m/validate [:set :int] #{42 105})
(m/validate [:set :int] #{:a :b})