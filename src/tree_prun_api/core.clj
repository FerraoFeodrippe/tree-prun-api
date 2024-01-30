(ns tree-prun-api.core
  (:require [tree-prun-api.infra.repository :as r]
            [ring.middleware.defaults :refer [wrap-defaults
                                              site-defaults]]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :refer [not-found]]
            [ring.adapter.jetty :refer [run-jetty]]
            [clojure.data.json :as json]
            [clojure.string :refer [blank?]]
            [clj-http.client :as http]
            [tree-prun-api.config :as c])
  (:gen-class))

(defn- coords-str-to-double
  [coords]
  (mapv #(Double/parseDouble %)
        (if (blank? (str coords))
          []
          (if (vector? coords) 
            coords 
            [coords]))))

(defn- response
  [data]
  (-> data json/write-str))

(defonce system
  (let [config (-> "ENV" System/getProperty keyword c/make-config)]
    (c/make-system {:config config})))

(defroutes api-methods
  (GET "/swagger" [] "OK, that is working. Swagger in development...")

  (GET "/poles/:feeder_circuit_operational_id"
    {{feeder-circuit-operational-id  :feeder_circuit_operational_id} :params
     {coords "coords"} :query-params}
    (response
     (r/get-poles
      system
      feeder-circuit-operational-id
      (coords-str-to-double coords))))

  (GET "/power_transformers"
    {{coords "coords"} :query-params}
    (response
     (r/get-power-transformers
      system
      (coords-str-to-double coords))))

  (GET "/switches/:feeder_circuit_operational_id"
    {{feeder-circuit-operational-id  :feeder_circuit_operational_id} :params
     {coords "coords"} :query-params}
    (response
     (r/get-switches
      system
      feeder-circuit-operational-id
      (coords-str-to-double coords))))

  (GET "/towers/:feeder_circuit_operational_id"
    {{feeder-circuit-operational-id  :feeder_circuit_operational_id} :params
     {coords "coords"} :query-params}
    (response
     (r/get-towers
      system
      feeder-circuit-operational-id
      (coords-str-to-double coords))))

  (GET "/wires/:feeder_circuit_operational_id"
    {{feeder-circuit-operational-id  :feeder_circuit_operational_id} :params
     {coords "coords"} :query-params}
    (response
     (r/get-wires
      system
      feeder-circuit-operational-id
      (coords-str-to-double coords))))

  (GET "/trees_pruning/:feeder_circuit_operational_id"
    {{feeder-circuit-operational-id  :feeder_circuit_operational_id} :params}
    (response
     (r/get-trees-pruning
      system
      feeder-circuit-operational-id)))

  (POST "/trees_pruning/create" {body :body}
    (let [data (-> body slurp (json/read-str {:key-fn keyword}))]
      (response
       (r/insert-tree-pruning system data))))

    (GET "/service_orders"
    {}
    (response
     (r/get-service-orders system)))

  (POST "/services_order/create" {body :body}
    (let [data (-> body slurp (json/read-str {:key-fn keyword}))
          date-created (new java.util.Date)]
      (response
       (r/insert-service-order system
                               (assoc data :date_created date-created)))))

  (not-found "<h1>Page not found</h1>"))

(def site
  (wrap-defaults
   api-methods
   (assoc-in site-defaults [:security :anti-forgery] false)))

(when @(:server system)
  (-> @(system :server) .stop))

(reset! (:server system) 
  (run-jetty site {:port 3000 :join? false}))

(comment

  ;;;;some tests 
  system
  (let [config (-> "ENV" System/getProperty keyword c/make-config)]
    (c/make-system {:config config
                    :server-handler site}))
  (-> (http/request {:url "http://localhost:3000/poles/REC_01"
                     :method :get})
      :body
      (json/read-str {:key-fn keyword}))

  (-> (http/request {:url "http://localhost:3000/trees_pruning/REC_01"
                     :method :get})
      :body
      (json/read-str {:key-fn keyword}))

  (-> (http/request {:url "http://localhost:3000/trees_pruning/REC_02"
                     :method :get})
      :body
      (json/read-str {:key-fn keyword}))

  (-> (http/request {:url "http://localhost:3000/service_orders"
                     :method :get
                     :throw-exceptions false})
      :body
      (json/read-str {:key-fn keyword}))


  (http/request {:url "http://localhost:3000/trees_pruning/create"
                 :method :post
                 :content-type :json
                 :form-params {:species			"s1"
                               :pole_id			"1"
                               :latitude			"1"
                               :longitude			"1"
                               :pruning_date		"2022-02-02"
                               :height				"10"
                               :diameter			"1"
                               :distance_at		"2"
                               :distance_bt		"2"
                               :distance_mt		"2"
                               :feeder_circuit_operational_id "REC_02"}})

  (http/request {:url "http://localhost:3000/services_order/create"
                 :method :post
                 :content-type :json
                 :form-params {:description			"service order 1"
                               :classification			"Pode"
                               :tree_pruning_id		"99"
                               :observation			"need check date avaliable"
                               :status		"0"}})

  (r/get-poles system "REC_01")

  (r/get-poles system "REC_01" [1.0 1.0 1.0 1.0])

  (r/get-power-transformers system [])

  (r/get-power-transformers system [1.0 1.0 1.0 1.0])

  (r/get-switches system "REC_01")

  (r/get-switches system "REC_02" [1.0 1.0 1.0 1.0])

  (r/get-towers system "REC_01")

  (r/get-towers system "REC_01" [1.0 1.0 1.0 1.0])

  (r/get-wires system "REC_01")

  (r/get-wires system "REC_01" [1.0 2.0 1.0 2.0])

  )



