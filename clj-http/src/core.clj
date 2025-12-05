(ns core
  (:require [clj-http.client :as client]))

(def base-url "https://openholidaysapi.org/")
(def default-opts {:accept :json})

(defn- path->url [path]
  (str base-url path))

(defn get
  ([path]
   (get path {}))
  ([path opts]
   (client/get (path->url path)
               (merge default-opts opts))))

(defn get-async [path]
  (client/get (path->url path)
              (merge default-opts {:async? true})
              (fn [response] (println "Success:" (:body response)))
              (fn [exception] (println "Error:" (.getMessage exception)))))

(comment
  (get "Countries")
  (get "Languages")
  (get "PublicHolidays"
       {:query-params {"countryIsoCode"  "BR"
                       "languageIsoCode" "PT"
                       "validFrom"       "2025-01-01"
                       "validTo"         "2025-12-31"}})
  (get-async "Countries")
  (get-async "NonExistentEndpoint")
  )