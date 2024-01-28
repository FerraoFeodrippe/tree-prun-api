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
            geoCoordinate
            feederCircuits
            zone])

(defrecord PowerTransformer
           [id
            description
            geoCoordinate])

(defrecord Switch
           [id
            description
            feederCircuit 
            switchClassification
            geoCoordinate])

(defrecord Tower
           [id
            description
            feederCircuit 
            geoCoordinate
            zone
            height])

(defrecord Wire
           [id
            description 
            feederCircuit
            network
            wireSpecification
            wireGauge
            zone
            geoCoordinate1
            geoCoordinate2
            wireLength])

;;;; Tree Management

(defrecord OperationalBase
           [id
            name
            geoCoordinate])

(defrecord Team
           [id
            name
            servicesClassification
            operationalBases])

(defrecord TreePruning
           [id
            species
            pole
            geoCoordinate
            pruningDate
            height
            diameter
            distanceAt
            distanceBt
            distanceMt])

(defrecord ServiceOrder
           [id
            description
            classification
            treePruning
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
   :operational-nase ->OperationalBase
   :serviceOrder ->ServiceOrder
   :team ->Team
   :tree-pruning ->TreePruning})
