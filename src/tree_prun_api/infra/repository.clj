(ns tree-prun-api.infra.repository
  (:require [tree-prun-api.domain :as d :refer [AGisRepository 
                                                DataResponse
                                                make-entity
                                                ->GeoCoordinate]]
            [tree-prun-api.infra.scripts :refer [scripts]]
            [clojure.java.jdbc :refer [query execute!]])
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
    #(-> % :feeder_operational_ids (.split ","))
    :zone]
   
   :powerTranformer 
   [:id :description makeGeoCoordinate]

   :switch
   [:id :description :switch_classification makeGeoCoordinate]

   :tower 
   [:id :description makeGeoCoordinate]

   :wire
   [:id 
    :description 
    :network 
    :wire_specification
    :zone
    :wireGauge 
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

(deftype GisRepository [] 
  AGisRepository
  ;; (getFeederCircuits [_ dataRequest]
  ;;   (makeResponse dataRequest :feederCircuit :getFeederCircuits))

  (getPoles [_ dataRequest]
    "when dataRequest has 1 parameter :getPoles script will be passed and search by fedderCircuitId
     when dataRequest has 5 parameter :getPolesFilterCoords script will be passed and the first parameter is fedderCircuitId and 4 last are latitude and longitude range, in order."
    (case (count dataRequest)
       1 (makeResponse dataRequest :pole :getPoles)
       5 (makeResponse dataRequest :pole :getPolesFilterCoords)
       (DataResponse
        :error
        nil
        "dataRequest has not the right number of parameters")))

  (getPowerTransformers [_ dataRequest]
    (makeResponse  dataRequest :powerTranformer :getPowerTranformers))

  (getSwitches [_ dataRequest]
    (makeResponse dataRequest :switch :getSwitches))

  (getTowers [_ dataRequest] 
    (makeResponse dataRequest :tower :getTowers))

  (getWires [_ dataRequest] 
    (makeResponse dataRequest :wire :getWires)))

;;;; Those below funcions are just will help to insert data for simplicity
;;;; for some operations what are not designed for




