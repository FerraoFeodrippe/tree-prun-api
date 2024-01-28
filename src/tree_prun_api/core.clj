(ns tree-prun-api.core
  (:require [tree-prun-api.infra.repository :as r]
            [ring.middleware.defaults :refer [wrap-defaults
                                              site-defaults]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [not-found]]
            [ring.adapter.jetty :refer [run-jetty]]
            [clojure.data.json :as json]
            [clojure.string :refer [blank?]]
            [clj-http.client :as http]
            )
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

(defroutes api-methods
  (GET "/swagger" [] "OK, that is working. Swagger in development...")

  (GET "/poles/:feeder_circuit_operational_id"
    {{feeder-circuit-operational-id  :feeder_circuit_operational_id} :params
     {coords "coords"} :query-params}
    (response
     (r/get-poles
        feeder-circuit-operational-id
        (coords-str-to-double coords))))

  (GET "/power_transformers"
    {{coords "coords"} :query-params}
    (response
     (r/get-power-transformers
      (coords-str-to-double coords))))

  (GET "/switches/:feeder_circuit_operational_id"
    {{feeder-circuit-operational-id  :feeder_circuit_operational_id} :params
     {coords "coords"} :query-params}
    (response
     (r/get-switches      
      feeder-circuit-operational-id
      (coords-str-to-double coords))))

  (GET "/towers/:feeder_circuit_operational_id"
    {{feeder-circuit-operational-id  :feeder_circuit_operational_id} :params
     {coords "coords"} :query-params}
    (response
     (r/get-towers
      feeder-circuit-operational-id
      (coords-str-to-double coords))))

  (GET "/wires/:feeder_circuit_operational_id"
    {{feeder-circuit-operational-id  :feeder_circuit_operational_id} :params
     {coords "coords"} :query-params}
    (response
     (r/get-wires
      feeder-circuit-operational-id
      (coords-str-to-double coords))))

  (not-found "<h1>Page not found</h1>"))

(def site
  (wrap-defaults api-methods site-defaults))

(defonce server (atom nil))

(when @server
  (.stop @server))

(reset! server 
  (run-jetty site {:port 3000 :join? false}))

(comment
  ;;;;some tests 
  (bean (type @server))

  (-> (http/request {:url "http://localhost:3000/poles/REC_01"
                     :method :get})
      :body
      (json/read-str {:key-fn keyword}))

  (r/get-poles "REC_01")

  (r/get-poles "REC_01" [1.0 1.0 1.0 1.0])

  (r/get-power-transformers [])

  (r/get-power-transformers [1.0 1.0 1.0 1.0])

  (r/get-switches "REC_01")

  (r/get-switches "REC_02" [1.0 1.0 1.0 1.0])

  (r/get-towers "REC_01")

  (r/get-towers "REC_01" [1.0 1.0 1.0 1.0])

  (r/get-wires "REC_01")

  (r/get-wires "REC_01" [1.0 2.0 1.0 2.0])
  )



