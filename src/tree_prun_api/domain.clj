(ns tree-prun-api.domain
  (:gen-class))

;;;; Commom
(def data-response-status #{:ok :error})

(defn make-data-response
  ([status data]
   (make-data-response status data nil))
  ([status data errors]
   {:pre [(status data-response-status)]}
   {:status status :data data :errors errors}))

;;;; ObjectValues
(defrecord GeoCoordinate [latitude longitude])

;;;; GIS

(defrecord FeederCircuit [id operational_id name geoCoordinate])

(defrecord Pole 
           [id
            description
            geo_coordinate
            feeder_circuits
            zone])

(defrecord PowerTransformer
           [id
            description
            geo_coordinate])

(defrecord Switch
           [id
            description
            feeder_circuit 
            switch_classification
            geo_coordinate])

(defrecord Tower
           [id
            description
            feeder_Circuit 
            geo_coordinate
            zone
            height])

(defrecord Wire
           [id
            description 
            feeder_circuit
            network
            wire_specification
            wireGauge
            zone
            geo_coordinate1
            geo_coordinate2
            wire_length])

;;;; Tree Management

(defrecord OperationalBase
           [id
            name
            geo_coordinate])

(defrecord Team
           [id
            name
            services_classification
            operational_base_id])

(defrecord TreePruning
           [id
            species
            pole
            geo_coordinate
            pruningDate
            height
            diameter
            distance_at
            distance_bt
            distance_mt
            feeder_circuit])

(defrecord ServiceOrder
           [id
            description
            classification
            tree_pruning
            status
            observation])

(def make-entity
  {;;GIS
   :feeder-circuit ->FeederCircuit
   :pole ->Pole
   :power-tranformer ->PowerTransformer
   :switch ->Switch
   :tower ->Tower
   :wire ->Wire
   ;;Tree Management
   :operational-base ->OperationalBase
   :service-order ->ServiceOrder
   :team ->Team
   :tree-pruning ->TreePruning})
