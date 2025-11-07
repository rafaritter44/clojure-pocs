(ns main
  (:require [io.pedestal.connector :as conn]
            [io.pedestal.http.http-kit :as hk]
            [io.pedestal.http.route :as route]
            [io.pedestal.connector.test :as test]
            [clojure.edn :as edn]))

(defn- response [status body & {:as headers}]
  {:status  status
   :body    body
   :headers headers})

(def ok (partial response 200))
(def created (partial response 201))

(defonce *database (atom {}))

(def db-interceptor
  {:name  :db-interceptor
   :enter (fn [context]
            (update context :request assoc :database @*database))
   :leave (fn [context]
            (if-let [tx-data (:tx-data context)]
              (let [database' (apply swap! *database tx-data)]
                (assoc-in context [:request :database] database'))
              context))})

(defn- make-list [list-name]
  {:name  list-name
   :items {}})

(defn- make-list-item
  ([item-name]
   (make-list-item item-name false))
  ([item-name done?]
   {:name  item-name
    :done? done?}))

(def echo
  {:name  :echo
   :enter (fn [context]
            (let [request  (:request context)
                  response (ok request)]
              (assoc context :response response)))})

(def list-create
  {:name  :list-create
   :enter (fn [context]
            (let [list-name (get-in context [:request :query-params :name] "Unnamed List")
                  new-list  (make-list list-name)
                  db-id     (str (gensym "l"))
                  url       (route/url-for :list-view :params {:list-id db-id})]
              (assoc context
                     :response (created new-list "Location" url)
                     :tx-data [assoc db-id new-list])))})

(defn- find-list-by-id [dbval db-id]
  (get dbval db-id))

(def list-view
  {:name  :list-view
   :leave (fn [context]
            (let [db-id    (get-in context [:request :path-params :list-id])
                  the-list (when db-id
                             (find-list-by-id
                              (get-in context [:request :database])
                              db-id))]
              (cond-> context
                the-list (assoc :result the-list))))})

(def entity-render
  {:name  :entity-render
   :leave (fn [context]
            (if-let [item (:result context)]
              (assoc context :response (ok item))
              context))})

(defn- find-list-item-by-ids [dbval list-id item-id]
  (get-in dbval [list-id :items item-id] nil))

(def list-item-view
  {:name  :list-item-view
   :leave (fn [context]
            (let [list-id (get-in context [:request :path-params :list-id])
                  item-id (and list-id
                               (get-in context [:request :path-params :item-id]))
                  item    (and item-id
                               (find-list-item-by-ids (get-in context [:request :database]) list-id item-id))]
              (cond-> context
                item (assoc :result item))))})

(defn- list-item-add
  [dbval list-id item-id new-item]
  (if (contains? dbval list-id)
    (assoc-in dbval [list-id :items item-id] new-item)
    dbval))

(def list-item-create
  {:name  :list-item-create
   :enter (fn [context]
            (if-let [list-id (get-in context [:request :path-params :list-id])]
              (let [item-name (get-in context [:request :query-params :name] "Unnamed Item")
                    new-item  (make-list-item item-name)
                    item-id   (str (gensym "i"))]
                (-> context
                    (assoc :tx-data [list-item-add list-id item-id new-item])
                    (assoc-in [:request :path-params :item-id] item-id)))
              context))})

(defn- list-item-update-fn
  [dbval list-id item-id updated-item]
  (if (and (contains? dbval list-id)
           (get-in dbval [list-id :items item-id]))
    (assoc-in dbval [list-id :items item-id] updated-item)
    dbval))

(def list-item-update
  {:name  :list-item-update
   :enter (fn [context]
            (let [{:keys [list-id item-id]} (get-in context [:request :path-params])
                  {:keys [name done]}       (get-in context [:request :query-params])
                  updated-item              (make-list-item name (parse-boolean done))]
              (assoc context
                     :tx-data [list-item-update-fn list-id item-id updated-item])))})

(defn- list-item-delete-fn
  [dbval list-id item-id]
  (if (and (contains? dbval list-id)
           (get-in dbval [list-id :items item-id]))
    (update-in dbval [list-id :items] dissoc item-id)
    dbval))

(def list-item-delete
  {:name  :list-item-delete
   :enter (fn [context]
            (let [{:keys [list-id item-id]} (get-in context [:request :path-params])]
              (assoc context
                     :tx-data [list-item-delete-fn list-id item-id])))})

(def routes
  #{["/echo" :get echo]
    ["/todo" :post [db-interceptor list-create]]
    ["/todo" :get echo :route-name :list-query-form]
    ["/todo/:list-id" :get [entity-render db-interceptor list-view]]
    ["/todo/:list-id" :post [entity-render list-item-view db-interceptor list-item-create]]
    ["/todo/:list-id/:item-id" :get [entity-render db-interceptor list-item-view]]
    ["/todo/:list-id/:item-id" :put [entity-render list-item-view db-interceptor list-item-update]]
    ["/todo/:list-id/:item-id" :delete [entity-render list-view db-interceptor list-item-delete]]})

(defn- create-connector []
  (-> (conn/default-connector-map 8890)
      (conn/with-default-interceptors)
      (conn/with-routes routes)
      (hk/create-connector nil)))

;; For interactive development
(defonce *connector (atom nil))

(defn- start []
  (reset! *connector
          (conn/start! (create-connector))))

(defn- stop []
  (conn/stop! @*connector)
  (reset! *connector nil))

(defn- restart []
  (stop)
  (start))

(defn- test-request [verb url]
  (test/response-for @*connector verb url))

(comment
  (main/start)
  (main/stop)
  (main/restart)
  (test-request :get "/does-not-exist")
  (test-request :post "/todo")
  (test-request :post (get-in *1 [:headers "Location"]))
  (test-request :get (get-in *2 [:headers "Location"]))
  (test-request :put (str
                      (get-in *3 [:headers "Location"])
                      "/"
                      (-> *1 :body edn/read-string :items keys first)
                      "?name=Updated+Item&done=true"))
  (test-request :get "/todo")
  (test-request :get (str
                      (get-in *3 [:headers "Location"])
                      "/"
                      (-> *1 :body edn/read-string :items keys first)))
  (test-request :delete "/todo/l26302/i26398")
  )