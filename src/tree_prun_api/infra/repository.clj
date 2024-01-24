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

;TODO: pass data to query
(defn- ExecuteQuery
  [script data]
  (query
   sqlite-db
   [script]))

(defn- ExecuteNonQuery
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
    #(map read-string (-> % :feeder_circuit_ids (.split ",")))
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

(defn DataConverter
  [data type]
  (pmap #(apply (type make-entity) (getProjection % type)) data))

(defn ExecuteScript
  [dataRequest type script]
  (if (nil? type)
    (ExecuteNonQuery (script scripts) dataRequest)
    (DataConverter (ExecuteQuery (script scripts) dataRequest) type)))

(defn makeResponse
  [dataRequest type script]
  (try
    (DataResponse :ok
                  (ExecuteScript dataRequest type script))
    (catch Exception e 
      (DataResponse :error nil (vector (.getMessage e))))))

(deftype GisRepository [] 
  AGisRepository
  (getFeederCircuits [_ dataRequest]
    (makeResponse dataRequest :feederCircuit :getFeederCircuits))

  (getPoles [_ dataRequest]
    (makeResponse dataRequest :pole :getPoles))

  (getPowerTransformers [_ dataRequest]
    (makeResponse  dataRequest :powerTranformer :getPowerTranformers))

  (getSwitch [_ dataRequest]
    (makeResponse dataRequest :switch :getSwitches))

  (getTowers [_ dataRequest] 
    (makeResponse dataRequest :tower :getTowers))

  (getWires [_ dataRequest] 
    (makeResponse dataRequest :wire :getWires)))

;;;; Those below funcions are just will help to insert data for simplicity
;;;; for some operations what are not designed for




