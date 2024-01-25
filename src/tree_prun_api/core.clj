(ns tree-prun-api.core
  (:require [tree-prun-api.infra.repository :as r :refer [->GisRepository]]
            [tree-prun-api.services]
            [ring.middleware.defaults :refer :all]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :refer [run-jetty]]
            [clojure.data.json :as json])
  (:gen-class))

(def rGis (->GisRepository))

(defroutes app-routes
  (GET "/swagger" [] "OK, that is working. Swagger in development...")
  (GET "/poles/:feeder_circuit_operational_id"
    {{feeder_circuit_operational_id  :feeder_circuit_operational_id} :params
     {coords "coords"} :query-params}
    (json/write-str
     (.getPoles
      rGis
      (into [feeder_circuit_operational_id]
            (map #(Double/parseDouble %) 
                 (if (vector? coords) coords [coords]))))))
  
  (route/not-found "<h1>Page not found</h1>"))

(def site
  (wrap-defaults app-routes site-defaults))

(run-jetty site {:port 3000
                 :join? false})

(comment
  ;;;;some tests
  
  (.getPoles rGis ["REC_01" 1 1 1 2])
  (.getPoles rGis ["REC_02" 1 1 1 2])
  (.getPoles rGis ["REC_02" 1 1 1 1])
  (.getPoles rGis ["REC_01" 1 1 1 1])
  (.getPoles rGis ["REC_03" 1 1 1 1])
  (.getPoles rGis ["REC_03" 1 1 2 2])

  (.getPowerTransformers rGis [])
  (.getPowerTransformers rGis [1 1 2 2])
  (.getPowerTransformers rGis [1 1 2 2])
  (.getPowerTransformers rGis [1 2 1 1])

  (.getSwitches rGis ["REC_01"])
  (.getSwitches rGis ["REC_02"])
  (.getSwitches rGis ["REC_01" 1 1 1 1])
  (.getSwitches rGis ["REC_01" 2 2 1 1])

  (.getTowers rGis ["REC_01"])
  (.getTowers rGis ["REC_02"])
  (.getTowers rGis ["REC_01" 1 1 1 1])
  (.getTowers rGis ["REC_01" 2 2 1 1])
  
  (.getWires rGis ["REC_01"])
  (.getWires rGis ["REC_02"])
  (.getWires rGis ["REC_01" 1 2 1 2])
  (.getWires rGis ["REC_01" 1 2 1 1])
  (.getWires rGis ["REC_01" 1 1 1 2])
  (.getWires rGis ["REC_01" 1 1 1 1])
  (.getWires rGis ["REC_01" 2 2 1 1])
  ;;(.getFeederCircuits rGis [])
  )



