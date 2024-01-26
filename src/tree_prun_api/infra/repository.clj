(ns tree-prun-api.infra.repository
  (:require [tree-prun-api.domain :as d :refer [AGisRepository 
                                                DataResponse
                                                make-entity
                                                ->GeoCoordinate]]
            [tree-prun-api.infra.scripts :refer [scripts]]
            [clojure.java.jdbc :refer [query execute!]]
            [clojure.string :refer [blank?]])
  (:gen-class))

(def sqlite-db
  {:classname "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "tree_prun.db"})

(defn- executeQuery
  [script data]
  (query
   sqlite-db 
   (into [script] data)))

(defn- executeNonQuery
  [script data]
  (execute! 
   sqlite-db
   [script data]
   {:multi? true}))

(defn makeGeoCoordinate
  [data]
  (->GeoCoordinate (:latitude data) (:longitude data)))

(def projections
  {;;GIS
   :feederCircuit
   [:id :name makeGeoCoordinate]

   :pole
   [:id :description makeGeoCoordinate
    ;;TOTHINK: should I mount full entity for feederCircuits or just ids? 
    ;;for now just ids
    #(-> % :feeder_circuit_operational_ids (.split ","))
    :zone]
   
   :powerTranformer 
   [:id :description makeGeoCoordinate]

   :switch
   [:id :description :feeder_circuit_operational_id 
    :switch_classification makeGeoCoordinate]

   :tower 
   [:id :description
    :feeder_circuit_operational_id makeGeoCoordinate
    :zone :height]

   :wire
   [:id 
    :description 
    :feeder_circuit_operational_id
    :network 
    :wire_specification
    :wire_gauge
    :zone 
    #(->GeoCoordinate (:latitude1 %) (:longitude1 %))
    #(->GeoCoordinate (:latitude2 %) (:longitude2 %))
    :wire_length]

   ;;Tree Management
   :operationalBase 
   [:id :name makeGeoCoordinate]

   :serviceOrder 
   [:id :description :classification :tree_pruning_id :observation]

   :team 
   [:id :name :services_classification]

   :treePruning 
   [:id :species :pole_id makeGeoCoordinate 
    :pruning_date :height :diameter
    :distance_at :distance_bt :distance_mt]})

(defn getProjection
  [data type]
  (map #(% data) (type projections)))

(defn dataConverter
  [data type]
  (pmap #(apply (type make-entity) (getProjection % type)) data))

(defn executeScript
  [dataRequest type script]
  (if (nil? type)
    (executeNonQuery (script scripts) dataRequest)
    (dataConverter (executeQuery (script scripts) dataRequest) type)))

(defn makeResponse
  [dataRequest type script]
  (try
    (DataResponse :ok
                  (executeScript dataRequest type script))
    (catch Exception e 
      (DataResponse :error nil (vector (.getMessage e))))))

(defn elementToVector
  [element]
  (if  (blank? (str element))
    []
    (if (vector? element)
      element
      [element])))

(defn makeDataRequest
  ([element]
   (elementToVector element))
  ([element & restElements]
   (into
    (elementToVector element)
    (apply makeDataRequest restElements))))

(deftype GisRepository []
  AGisRepository
  ;; (getFeederCircuits [_ dataRequest]
  ;; (makeResponse dataRequest :feederCircuit :getFeederCircuits))

  (getPoles [_ feeder_circuit_operational_id coords]
    "when dataRequest has 1 parameter getPoles script will be passed and search by fedderCircuitId
     when dataRequest has 5 parameters getPolesFilterCoords script will be passed and the first parameter is fedderCircuitId and last 4 are latitude and longitude range, in order."
    (let [dataRequest (makeDataRequest feeder_circuit_operational_id coords)
          pMakeResponse (partial makeResponse dataRequest :pole)]
      (case (count dataRequest)
        1 (pMakeResponse :getPoles)
        5 (pMakeResponse :getPolesFilterCoords)
        (DataResponse
         :error
         nil
         "dataRequest has not the right number of parameters"))))

  (getPowerTransformers [_ coords]
    "when dataRequest has 0 parameter getPowerTranformers script will be passed and search by fedderCircuitId
     when dataRequest has 4 parameters getPowerTranformersFilterCoords latitude and longitude range, in order."
    (let [dataRequest (makeDataRequest coords)
          pMakeResponse (partial makeResponse dataRequest :powerTranformer)]
      (case (count dataRequest)
        0 (pMakeResponse :getPowerTranformers)
        4 (pMakeResponse :getPowerTranformersFilterCoords)
        (DataResponse
         :error
         nil
         "dataRequest has not the right number of parameters"))))

  (getSwitches [_ feeder_circuit_operational_id coords]
    "when dataRequest has 1 parameter getSwitches script will be passed and search by fedderCircuitId
     when dataRequest has 5 parameters getSwitchesFilterCoords script will be passed and the first parameter is fedderCircuitId and last 4 are latitude and longitude range, in order."
    (let [dataRequest (makeDataRequest feeder_circuit_operational_id coords)
          pMakeResponse (partial makeResponse dataRequest :switch)]
      (case (count dataRequest)
        1 (pMakeResponse :getSwitches)
        5 (pMakeResponse :getSwitchesFilterCoords)
        (DataResponse
         :error
         nil
         "dataRequest has not the right number of parameters"))))

  (getTowers [_ feeder_circuit_operational_id coords]
    "when dataRequest has 1 parameter getTowers script will be passed and search by fedderCircuitId
     when dataRequest has 5 parameters getTowersFilterCoords script will be passed and the first parameter is fedderCircuitId and last 4 are latitude and longitude range, in order."
    (let [dataRequest (makeDataRequest feeder_circuit_operational_id coords)
          pMakeResponse (partial makeResponse dataRequest :tower)]
      (case (count dataRequest)
        1 (pMakeResponse :getTowers)
        5 (pMakeResponse :getTowersFilterCoords)
        (DataResponse
         :error
         nil
         "dataRequest has not the right number of parameters"))))

  (getWires [_ feeder_circuit_operational_id coords]
    "when dataRequest has 1 parameter getWires script will be passed and search by fedderCircuitId
     when dataRequest has 5 parameters getWiresFilterCoords script will be passed and the first parameter is fedderCircuitId and last 4 are latitude and longitude range, in order."


    (let [dataRequest (makeDataRequest feeder_circuit_operational_id coords)
          pMakeResponse (partial makeResponse (into dataRequest coords) :wire)]
      (case (count dataRequest)
        1 (pMakeResponse :getWires)
        5 (pMakeResponse :getWiresFilterCoords)
        (DataResponse
         :error
         nil
         "dataRequest has not the right number of parameters")))))
