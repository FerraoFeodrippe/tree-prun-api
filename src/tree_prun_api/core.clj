(ns tree-prun-api.core
  (:require [tree-prun-api.infra.repository :as r :refer [->GisRepository]])
  (:gen-class))

(def rGis (->GisRepository))

(comment
  *file*
  
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



