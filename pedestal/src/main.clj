(ns main
  (:require [io.pedestal.connector :as conn]
            [io.pedestal.http.http-kit :as hk]
            [io.pedestal.http.route :as route]
            [io.pedestal.connector.test :as test]))

(defn- response [status body & {:as headers}]
  {:status status :body body :headers headers})

(def ok (partial response 200))
(def created (partial response 201))
(def accepted (partial response 202))

(def echo
  {:name :echo
   :enter
   (fn [context]
     (let [request  (:request context)
           response (ok request)]
       (assoc context :response response)))})

(def routes
  #{["/todo" :post echo :route-name :list-create]
    ["/todo" :get echo :route-name :list-query-form]
    ["/todo/:list-id" :get echo :route-name :list-view]
    ["/todo/:list-id" :post echo :route-name :list-item-create]
    ["/todo/:list-id/:item-id" :get echo :route-name :list-item-view]
    ["/todo/:list-id/:item-id" :put echo :route-name :list-item-update]
    ["/todo/:list-id/:item-id" :delete echo :route-name :list-item-delete]})

(defn create-connector []
  (-> (conn/default-connector-map 8890)
      (conn/with-default-interceptors)
      (conn/with-routes routes)
      (hk/create-connector nil)))

;; For interactive development
(defonce *connector (atom nil))

(defn start []
  (reset! *connector
          (conn/start! (create-connector))))

(defn stop []
  (conn/stop! @*connector)
  (reset! *connector nil))

(defn restart []
  (stop)
  (start))

(comment
  (main/start))