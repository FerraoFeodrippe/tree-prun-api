(ns tree-prun-api.infra.repository
  (:require [tree-prun-api.domain :as d]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [clojure.string :refer [blank?]])
  (:gen-class))

(defn- execute-command
  [ds cmd data]
  (jdbc/execute!
   ds
   (into [cmd] data)
   {:builder-fn rs/as-unqualified-lower-maps}))

(defn- make-geo-coordinate
  [data]
  (d/->GeoCoordinate (:latitude data) (:longitude data)))

(def projections
  {;;GIS
   :feeder-circuit
   [:id :name make-geo-coordinate]

   :pole
   [:id :description make-geo-coordinate
    ;;TOTHINK: should I mount full entity for feederCircuits or just ids? 
    ;;for now just ids
    #(-> % :feeder_circuit_operational_ids (.split ","))
    :zone]

   :power-tranformer
   [:id :description make-geo-coordinate]

   :switch
   [:id :description :feeder_circuit_operational_id
    :switch_classification make-geo-coordinate]

   :tower
   [:id :description
    :feeder_circuit_operational_id make-geo-coordinate
    :zone :height]

   :wire
   [:id
    :description
    :feeder_circuit_operational_id
    :network
    :wire_specification
    :wire_gauge
    :zone
    #(d/->GeoCoordinate (:latitude1 %) (:longitude1 %))
    #(d/->GeoCoordinate (:latitude2 %) (:longitude2 %))
    :wire_length]

   ;;Tree Management
   :operational-base
   [:id :name make-geo-coordinate]

   :service-order
   [:id :description :classification :tree_pruning_id :status :observation]

   :team
   [:id :name :services_classification]

   :tree-pruning
   [:id :species :pole_id make-geo-coordinate
    :pruning_date :height :diameter
    :distance_at :distance_bt :distance_mt
    :feeder_circuit_operational_id]})

(defn- get-projection
  [data type]
  (map #(% data) (type projections)))

(defn- dataConverter
  [data type]
  (pmap #(apply (type d/make-entity) (get-projection % type)) data))

(defn- execute-script
  [system dataRequest type script]
  (try
    (let [scripts-binds-from (:scripts-binds-from system)
          ds ((script scripts-binds-from) (:ds system))
          result (execute-command ds (script (:scripts system)) dataRequest)] 
      (d/make-data-response
       :ok
       (if type
         (dataConverter result type)
         result)))
    (catch Exception e
      (d/make-data-response :error nil (vector (.getMessage e))))))

(defn- element-to-vector
  [element]
  (if  (blank? (str element))
    []
    (if (vector? element)
      element
      [element])))

(defn- make-data-request
  ([element]
   (element-to-vector element))
  ([element & restElements]
   (into
    (element-to-vector element)
    (apply make-data-request restElements))))

(defn get-poles
  ([system feeder-circuit-operational-id] 
   (get-poles system feeder-circuit-operational-id [])) 
  ([system feeder-circuit-operational-id coords] 
   (let [data-request (make-data-request feeder-circuit-operational-id coords)
         p-execute-script (partial execute-script system data-request :pole)]
     (case (count data-request)
       1 (p-execute-script :get-poles)
       5 (p-execute-script :get-poles-filter-coords)
       (d/make-data-response
        :error
        nil
        "dataRequest has not the right number of parameters")))))

(defn get-power-transformers
  [system coords]
  (let [data-request (make-data-request coords)
        p-execute-script (partial execute-script system data-request :power-tranformer)]
    (case (count data-request)
      0 (p-execute-script :get-power-tranformers)
      4 (p-execute-script :get-power-tranformers-filter-coords)
      (d/make-data-response
       :error
       nil
       "dataRequest has not the right number of parameters"))))

(defn get-switches
  ([system feeder-circuit-operational-id]
   (get-switches system feeder-circuit-operational-id []))
  ([system feeder-circuit-operational-id coords]
   (let [data-request (make-data-request feeder-circuit-operational-id coords)
         p-execute-script (partial execute-script system data-request :switch)]
     (case (count data-request)
       1 (p-execute-script :get-switches)
       5 (p-execute-script :get-switches-filter-coords)
       (d/make-data-response
        :error
        nil
        "dataRequest has not the right number of parameters")))))

(defn get-towers
  ([system feeder-circuit-operational-id]
   (get-towers system feeder-circuit-operational-id []))
  ([system feeder-circuit-operational-id coords]
   (let [data-request (make-data-request feeder-circuit-operational-id coords)
         p-execute-script (partial execute-script system data-request :tower)]
     (case (count data-request)
       1 (p-execute-script :get-towers)
       5 (p-execute-script :get-towers-filter-coords)
       (d/make-data-response
        :error
        nil
        "dataRequest has not the right number of parameters")))))

(defn get-wires
  ([system feeder-circuit-operational-id]
   (get-wires system feeder-circuit-operational-id []))
  ([system feeder-circuit-operational-id coords]
   (let [data-request (make-data-request feeder-circuit-operational-id coords)
         p-execute-script (partial execute-script system (into data-request coords) :wire)]
     (case (count data-request)
       1 (p-execute-script :get-wires)
       5 (p-execute-script :get-wires-filter-coords)
       (d/make-data-response
        :error
        nil
        "dataRequest has not the right number of parameters")))))

(defn get-trees-pruning
  [system feeder-circuit-operational-id]
  (let [data-request (make-data-request feeder-circuit-operational-id)
        p-execute-script (partial execute-script system data-request :tree-pruning)]
    (case (count data-request)
      1 (p-execute-script :get-trees-pruning)
      (d/make-data-response
       :error
       nil
       "dataRequest has not the right number of parameters"))))

(defn insert-tree-pruning
  [system {:keys [species
                  pole_id
                  latitude
                  longitude
                  pruning_date
                  height
                  diameter
                  distance_at
                  distance_bt
                  distance_mt
                  feeder_circuit_operational_id]}]
  (let [data-request (make-data-request species
                                       pole_id
                                       latitude
                                       longitude
                                       pruning_date
                                       height
                                       diameter
                                       distance_at
                                       distance_bt
                                       distance_mt
                                       feeder_circuit_operational_id)]
    (case (count data-request)
      11 (execute-script system data-request nil :insert-tree-pruning)
      (d/make-data-response
       :error
       nil
       "dataRequest has not the right number of parameters"))))

(defn get-service-orders
  [system]
  (let [data-request []
        p-execute-script (partial execute-script system data-request :service-order)]
    (case (count data-request)
      0 (p-execute-script :get-service-orders)
      (d/make-data-response
       :error
       nil
       "dataRequest has not the right number of parameters"))))

(defn insert-service-order
  [system {:keys [description
                  classification
                  tree_pruning_id
                  observation
                  status
                  date_created]}]
  (let [data-request (make-data-request description
                                       classification
                                       tree_pruning_id
                                       observation
                                       status
                                       date_created)]
    
    (case (count data-request)
      6 (execute-script system data-request nil :insert-service-order)
      (d/make-data-response
       :error
       nil
       "dataRequest has not the right number of parameters"))))
