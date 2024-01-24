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

(defrecord FeederCircuit [id name geoCoordinate])

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
            switchClassification
            geoCoordinate])

(defrecord Tower
           [id
            description
            geoCoordinate
            zone
            height])

(defrecord Wire
           [id
            description
            network
            wireSpecification
            zone
            wireGauge
            geoCoordinate1
            geoCoordinate2
            wireLength])

;;;;;;;; GIS Contracts
(defprotocol AGisRepository
  "Repository contract for GIS"
  (getFeederCircuits [this dataRequest])
  (getPoles [this dataRequest])
  (getPowerTransformers [this dataRequest])
  (getSwitch [this dataRequest])
  (getTowers [this dataRequest])
  (getWires [this dataRequest]))

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
