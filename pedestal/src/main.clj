(ns main
  (:require [io.pedestal.connector :as conn]
            [io.pedestal.http.http-kit :as hk]
            [io.pedestal.http.route :as route]
            [io.pedestal.connector.test :as test]))

(defn- response [status body & {:as headers}]
  {:status status :body body :headers headers})

(def ok (partial response 200))
(def created (partial response 201))

(defonce *database (atom {}))

(def db-interceptor
  {:name :db-interceptor
   :enter
   (fn [context]
     (update context :request assoc :database @*database))
   :leave
   (fn [context]
     (if-let [tx-data (:tx-data context)]
       (let [database' (apply swap! *database tx-data)]
         (assoc-in context [:request :database] database'))
       context))})

(defn- make-list [list-name]
  {:name  list-name
   :items {}})

(defn- make-list-item [item-name]
  {:name  item-name
   :done? false})

(def echo
  {:name :echo
   :enter
   (fn [context]
     (let [request  (:request context)
           response (ok request)]
       (assoc context :response response)))})

(def list-create
  {:name :list-create
   :enter
   (fn [context]
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
  {:name :list-view
   :enter
   (fn [context]
     (let [db-id    (get-in context [:request :path-params :list-id])
           the-list (when db-id
                      (find-list-by-id
                       (get-in context [:request :database])
                       db-id))]
       (cond-> context
         the-list (assoc :result the-list))))})

(def entity-render
  {:name :entity-render
   :leave
   (fn [context]
     (if-let [item (:result context)]
       (assoc context :response (ok item))
       context))})

(defn- find-list-item-by-ids [dbval list-id item-id]
  (get-in dbval [list-id :items item-id] nil))

(def list-item-view
  {:name :list-item-view
   :leave
   (fn [context]
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
  {:name :list-item-create
   :enter
   (fn [context]
     (if-let [list-id (get-in context [:request :path-params :list-id])]
       (let [item-name (get-in context [:request :query-params :name] "Unnamed Item")
             new-item  (make-list-item item-name)
             item-id   (str (gensym "i"))]
         (-> context
             (assoc :tx-data [list-item-add list-id item-id new-item])
             (assoc-in [:request :path-params :item-id] item-id)))
       context))})

(def routes
  #{["/todo" :post [db-interceptor list-create]]
    ["/todo" :get echo :route-name :list-query-form]
    ["/todo/:list-id" :get [entity-render db-interceptor list-view]]
    ["/todo/:list-id" :post [entity-render list-item-view db-interceptor list-item-create]]
    ["/todo/:list-id/:item-id" :get [entity-render db-interceptor list-item-view]]
    ["/todo/:list-id/:item-id" :put echo :route-name :list-item-update]
    ["/todo/:list-id/:item-id" :delete echo :route-name :list-item-delete]})

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
  (test-request :post "/todo")
  (test-request :get "/todo")
  (dissoc *1 :body)
  (test-request :get "/does-not-exist")
  (test-request :get "/todo/l26478")
  (test-request :post "/todo/l26478")
  (test-request :get "/todo/l26478/i26481")
  (test-request :put "/todo/l26478/i26481")
  (test-request :delete "/todo/l26478/i26481"))