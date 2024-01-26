(ns tree-prun-api.domain
  (:gen-class))

;;;; Commom
(def DataResponseStatus #{:ok :error})

(defn DataResponse 
  ([status data]
   (DataResponse status data nil))
  ([status data errors]
   {:pre [(status DataResponseStatus)]}
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

;;;;;;;; GIS Contracts
(defprotocol AGisRepository
  "Repository contract for GIS"
  ;;(getFeederCircuits [this dataRequest])
  (getPoles [this {:keys [feeder_circuit_operational_id, coords]}] )
  (getPowerTransformers [this {:keys [coords]}])
  (getSwitches [this {:keys [feeder_circuit_operational_id, coords]}])
  (getTowers [this {:keys [feeder_circuit_operational_id, coords]}])
  (getWires [this {:keys [feeder_circuit_operational_id, coords]}]))

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

;;;;;;;; Tree Management Contracts

(defprotocol ATreeRepository
  "Repository contract for Trees"
  (getTrees [dataRequest]))

(defprotocol AServiceOrderRepository
  "Repository contract for Services Orders"
  (getServicesOrders [dataRequest])
  (getTeams [dataRequest])
  (getOperationalBases [dataRequest]))


(def make-entity
  {;;GIS
   :feederCircuit ->FeederCircuit
   :pole ->Pole
   :powerTranformer ->PowerTransformer
   :switch ->Switch
   :tower ->Tower
   :wire ->Wire
   ;;Tree Management
   :operationalBase ->OperationalBase
   :serviceOrder ->ServiceOrder
   :team ->Team
   :treePruning ->TreePruning})
