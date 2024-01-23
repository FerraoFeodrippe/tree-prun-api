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
(defrecord GeoCoordinate [Latitude Longitude])

(defrecord UnitMeasure [unitInMeters])

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
  (getFeederCircuits [dataRequest])
  (getPoles [dataRequest])
  (getPowerTransformers [dataRequest])
  (getSwitch [dataRequest])
  (getTowers [dataRequest])
  (getWires [dataRequest]))

;;;; Tree Management

(defrecord OperationalBase
           [id
            name
            geoCoordinate])

(defrecord ServiceOrder
           [id
            description
            classification
            treePruning
            observation])

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
