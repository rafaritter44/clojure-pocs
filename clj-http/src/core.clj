(ns core
  (:require [clj-http.client :as client]))

(def base-url "https://openholidaysapi.org/")
(def default-opts {:accept :json})

(defn- path->url [path]
  (str base-url path))

(defn path->response
  ([path]
   (path->response path {}))
  ([path opts]
   (client/get (path->url path)
               (merge default-opts opts))))

(comment
  (path->response "Countries")
  (path->response "Languages")
  (path->response "PublicHolidays"
                  {:query-params {"countryIsoCode"  "BR"
                                  "languageIsoCode" "PT"
                                  "validFrom"       "2025-01-01"
                                  "validTo"         "2025-12-31"}})
  )