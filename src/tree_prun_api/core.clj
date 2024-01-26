(ns tree-prun-api.core
  (:require [tree-prun-api.infra.repository :as r :refer [->GisRepository]]
            [tree-prun-api.services]
            [ring.middleware.defaults :refer [wrap-defaults
                                              site-defaults]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [not-found]]
            [ring.adapter.jetty :refer [run-jetty]]
            [clojure.data.json :as json]
            [clojure.string :refer [blank?]])
  (:gen-class))

(def rGis (->GisRepository))

;;;; put that on util stuff?
(defn- coordsStrToDouble
  [coords]
  (mapv #(Double/parseDouble %)
        (if (blank? (str coords))
          []
          (if (vector? coords) 
            coords 
            [coords]))))

(defn- response
  [data]
  (json/write-str data))

(defroutes api-methods
  (GET "/swagger" [] "OK, that is working. Swagger in development...")

  (GET "/poles/:feeder_circuit_operational_id"
    {{feeder_circuit_operational_id  :feeder_circuit_operational_id} :params
     {coords "coords"} :query-params}
    (response
     (.getPoles
      rGis 
      feeder_circuit_operational_id 
      (coordsStrToDouble coords))))

  (GET "/power_transformers"
    {{coords "coords"} :query-params}
    (response
     (.getPowerTransformers
      rGis
      (coordsStrToDouble coords))))

  (GET "/switches/:feeder_circuit_operational_id"
    {{feeder_circuit_operational_id  :feeder_circuit_operational_id} :params
     {coords "coords"} :query-params}
    (response
     (.getSwitches
      rGis
      feeder_circuit_operational_id
      (coordsStrToDouble coords))))

  (GET "/towers/:feeder_circuit_operational_id"
    {{feeder_circuit_operational_id  :feeder_circuit_operational_id} :params
     {coords "coords"} :query-params}
    (response
     (.getTowers
      rGis
      feeder_circuit_operational_id
      (coordsStrToDouble coords))))

  (GET "/wires/:feeder_circuit_operational_id"
    {{feeder_circuit_operational_id  :feeder_circuit_operational_id} :params
     {coords "coords"} :query-params}
    (response
     (.getWires
      rGis
      feeder_circuit_operational_id
      (coordsStrToDouble coords))))

  (not-found "<h1>Page not found</h1>"))

(def site
  (wrap-defaults api-methods site-defaults))

(run-jetty site {:port 3000 :join? false})

(comment
  ;;;;some tests

  (.getPoles rGis
             {:feeder_circuit_operational_id "REC_01"})

  (.getPoles rGis
             {:feeder_circuit_operational_id "REC_01"
              :coords [1.0 1.0 1.0 1.0]})

  (.getPowerTransformers rGis  {})

  (.getPowerTransformers rGis
                         {:coords [1.0 1.0 1.0 1.0]})

  (.getSwitches rGis
                {:feeder_circuit_operational_id "REC_01"})

  (.getSwitches rGis
                {:feeder_circuit_operational_id "REC_02"
                 :coords [1.0 1.0 1.0 1.0]})

  (.getTowers rGis
              {:feeder_circuit_operational_id "REC_01"})

  (.getTowers rGis
              {:feeder_circuit_operational_id "REC_01"
               :coords [1.0 1.0 1.0 1.0]})

  (.getWires rGis
              {:feeder_circuit_operational_id "REC_01"})

  (.getWires rGis
              {:feeder_circuit_operational_id "REC_01"
               :coords [1.0 2.0 1.0 2.0]})
  )



