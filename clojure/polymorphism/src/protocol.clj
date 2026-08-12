(ns protocol)

(defprotocol Greeter
  "A protocol for greeting people in a specific language."
  (say-good-morning [greeter] [greeter greeted] "Say good morning to someone.")
  (say-good-evening [greeter] [greeter greeted] "Say good evening to someone."))

(defrecord FrenchGreeter [name]
  Greeter
  (say-good-morning [_]
    "Bonjour.")
  (say-good-morning [_ greeted]
    (str "Bonjour, " greeted "."))
  (say-good-evening [_]
    "Bonsoir.")
  (say-good-evening [_ greeted]
    (str "Bonsoir, " greeted ".")))

(defrecord ItalianGreeter [name]
  Greeter
  (say-good-morning [_]
    "Buongiorno.")
  (say-good-morning [_ greeted]
    (str "Buongiorno, " greeted "."))
  (say-good-evening [_]
    "Buonasera.")
  (say-good-evening [_ greeted]
    (str "Buonasera, " greeted ".")))

(def french-greeter (->FrenchGreeter "Jean"))
(def italian-greeter (->ItalianGreeter "Giovanni"))

(comment
  (:name french-greeter)
  (say-good-morning french-greeter)
  (say-good-morning french-greeter "Pierre")
  (say-good-evening french-greeter)
  (say-good-evening french-greeter "Pierre")

  (:name italian-greeter)
  (say-good-morning italian-greeter)
  (say-good-morning italian-greeter "Pietro")
  (say-good-evening italian-greeter)
  (say-good-evening italian-greeter "Pietro")
  )