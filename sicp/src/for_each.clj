(ns for-each
  (:require [cons :refer [cons car cdr]]))

(defn for-each [proc list]
  (cond (nil? list) "done"
        :else (do (proc (car list))
                  (for-each proc
                            (cdr list)))))

(defn for-each [proc list]
  (when-not (nil? list)
    (proc (car list))
    (recur proc (cdr list))))

(def one-to-four
  (cons 1
        (cons 2
              (cons 3
                    (cons 4 nil)))))

(comment
  (for-each println one-to-four)
  )