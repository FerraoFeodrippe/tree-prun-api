(ns tree-prun-api.infra.repository
  (:require [tree-prun-api.domain :as d :refer :all]
            [tree-prun-api.infra.scripts :refer [queries]])
  (:gen-class))

(defn- ReadData
  [query data]
  [[1 2 3 4 5] [6 7 8 9 10] [11 12 13 14 15]]) ;;;; Teste com mock fixo

(defn DataConverter
  [data type]
  (pmap #(apply (type make-entity) %) data))

(defn ExecuteScript
  [dataRequest type script]
  (println script)
  (println dataRequest)
  (DataConverter (ReadData (script queries) dataRequest) type))

(defn MakeResponse
  [dataRequest type script]
  (try
    (DataResponse :ok
                  (ExecuteScript dataRequest type script))
    (catch Exception e 
      (DataResponse :error nil (vector (.getMessage e))))))

(deftype GisRepository [] 
  AGisRepository
  (getFeederCircuits [dataRequest]
    (MakeResponse dataRequest :feederCircuit :getFeederCircuits))

  (getPoles [dataRequest]
    (MakeResponse dataRequest :pole :getPoles))

  (getPowerTransformers [dataRequest]
    (MakeResponse  dataRequest :powerTranformer :getPowerTranformers))

  (getSwitch [dataRequest]
    (MakeResponse dataRequest :switch :getSwitches))

  (getTowers [dataRequest] 
    (MakeResponse dataRequest :tower :getTowers))

  (getWires [dataRequest] 
    (MakeResponse dataRequest :wire :getWires)))
