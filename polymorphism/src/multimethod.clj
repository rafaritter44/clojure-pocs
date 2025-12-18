(ns multimethod)

;; defmulti
(defmulti encounter
  (fn [x y]
    [(:Species x)
     (:Species y)]))

;; defmethod
(defmethod encounter [:Bunny :Lion]
  [b l] :run-away)
(defmethod encounter [:Lion :Bunny]
  [l b] :eat)
(defmethod encounter [:Lion :Lion]
  [l1 l2] :fight)
(defmethod encounter [:Bunny :Bunny]
  [b1 b2] :mate)
(def b1 {:Species :Bunny :other :stuff})
(def b2 {:Species :Bunny :other :stuff})
(def l1 {:Species :Lion :other :stuff})
(def l2 {:Species :Lion :other :stuff})
(encounter b1 b2)
(encounter b1 l1)
(encounter l1 b1)
(encounter l1 l2)

;; derive
(derive ::rect ::shape)
(derive ::square ::rect)

;; hierarchy queries
(parents ::rect)
(ancestors ::square)
(descendants ::shape)
(isa? ::square ::shape)
(isa? [::square ::rect] [::shape ::shape])
(isa? 7 7)

;; Java class hierarchy
(derive java.util.Map ::collection)
(derive java.util.Collection ::collection)
(isa? java.util.HashMap ::collection)
(isa? String Object)
(ancestors java.util.ArrayList)

;; unsupported, since class descendants are an open set:
; (descendants Object)

;; isa?-based dispatch
(defmulti foo class)
(defmethod foo ::collection [c] :a-collection)
(defmethod foo String [s] :a-string)
(foo [])
(foo (java.util.HashMap.))
(foo "bar")

;; prefer-method
(defmulti bar (fn [x y] [x y]))
(defmethod bar [::rect ::shape] [x y] :rect-shape)
(defmethod bar [::shapre ::rect] [x y] :shape-rect)
(prefer-method bar [::rect ::shape] [::shape ::rect])
(bar ::rect ::rect)

;; keyword as dispatch function
(defmulti area :Shape)
(defn rect [wd ht] {:Shape :Rect :wd wd :ht ht})
(defn circle [radius] {:Shape :Circle :radius radius})
(defmethod area :Rect [r]
  (* (:wd r) (:ht r)))
(defmethod area :Circle [c]
  (* Math/PI (:radius c) (:radius c)))
(defmethod area :default [x] :oops)
(def r (rect 4 13))
(def c (circle 12))
(area r)
(area c)
(area {})