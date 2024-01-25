(ns tree-prun-api.core
  (:require [tree-prun-api.infra.repository :as r :refer [->GisRepository]]
            [tree-prun-api.services]
            [ring.middleware.defaults :refer :all]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))

(defroutes app
  (GET "/" [] "OK, that is working...")
  (GET "/kiko" [] "kiko route...")
  (GET "/buba" [] "buba route...")
  (GET "/pimpo" [] "pimpo route...")
  (GET "/tirolito" [] "tiolito route...")
  (route/not-found "<h1>Page not found</h1>"))

(def site
  (wrap-defaults app site-defaults))

(run-jetty site {:port 3000
                 :join? false})

(def rGis (->GisRepository))

(comment
  ;;;;some tests
  
  (.getPoles rGis ["REC_01"])
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



