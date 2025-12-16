(ns streams
  (:require [cons :refer [cons car cdr]]))

(def cons-stream cons)
(def head car)
(def tail cdr)

(defn append-streams [s1 s2]
  (if (empty-stream? s1)
    s2
    (cons-stream
     (head s1)
     (append-streams (tail s1)
                     s2))))

(defn enum-interval [low high]
  (if (> low high)
    the-empty-stream
    (cons-stream
     low
     (enum-interval (inc low) high))))

(defn map [proc s]
  (if (empty-stream? s)
    the-empty-stream
    (cons-stream
     (proc (head s))
     (delay (map proc (tail s))))))

(defn filter [pred s]
  (cond
    (empty-stream? s) the-empty-stream
    (pred (head s))
    (cons-stream (head s)
                 (filter pred
                         (tail s)))
    :else (filter pred (tail s))))

(defn accumulate [combiner init-val s]
  (if (empty-stream? s)
    init-val
    (combiner (head s)
              (accumulate combiner
                          init-val
                          (tail s)))))

(defn enumerate-tree [tree]
  (if (leaf-node? tree)
    (cons-stream tree
                 the-empty-stream)
    (append-streams
     (enumerate-tree
      (left-branch tree))
     (enumerate-tree
      (right-branch tree)))))

(defn sum-odd-squares [tree]
  (accumulate
   +
   0
   (map
    square
    (filter odd?
            (enumerate-tree tree)))))

(defn odd-fibs [n]
  (accumulate
   cons
   ()
   (filter
    odd?
    (map fib (enum-interval 1 n)))))

(defn flatten [st-of-st]
  (accumulate append-streams
              the-empty-stream
              st-of-st))

(defn flatmap [f s]
  (flatten (map f s)))

;; Given n, find all pairs 0 < j < i <= n
;; such that i + j is prime.
(defn prime-sum-pairs [n])