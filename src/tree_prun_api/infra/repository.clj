(ns tree-prun-api.infra.repository
  (:require [tree-prun-api.domain :as d]
            [tree-prun-api.infra.scripts :refer [scripts scripts-binds-from]]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [clojure.string :refer [blank?]]
            [tree-prun-api.configurations :as config])
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
   [:id :description :classification :tree_pruning_id :observation]

   :team
   [:id :name :services_classification]

   :tree-pruning
   [:id :species :pole_id make-geo-coordinate
    :pruning_date :height :diameter
    :distance_at :distance_bt :distance_mt]})

(defn- get-projection
  [data type]
  (map #(% data) (type projections)))

(defn- dataConverter
  [data type]
  (pmap #(apply (type d/make-entity) (get-projection % type)) data))



(defn- execute-script
  [dataRequest type script]
  (try
    (let [ds ((scripts-binds-from script) config/map-ds)
          result (execute-command ds (script scripts) dataRequest)]
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
  ([feeder-circuit-operational-id] 
   (get-poles feeder-circuit-operational-id []))
  ([feeder-circuit-operational-id coords] 
   (let [dataRequest (make-data-request feeder-circuit-operational-id coords)
         p-execute-script (partial execute-script dataRequest :pole)]
     (case (count dataRequest)
       1 (p-execute-script :get-poles)
       5 (p-execute-script :get-poles-filter-coords)
       (d/make-data-response
        :error
        nil
        "dataRequest has not the right number of parameters")))))

(defn get-power-transformers
  [coords]
  (let [dataRequest (make-data-request coords)
        p-execute-script (partial execute-script dataRequest :power-tranformer)]
    (case (count dataRequest)
      0 (p-execute-script :get-power-tranformers)
      4 (p-execute-script :get-power-tranformers-filter-coords)
      (d/make-data-response
       :error
       nil
       "dataRequest has not the right number of parameters"))))

(defn get-switches
  ([feeder-circuit-operational-id]
   (get-switches feeder-circuit-operational-id []))
  ([feeder-circuit-operational-id coords]
   (let [dataRequest (make-data-request feeder-circuit-operational-id coords)
         p-execute-script (partial execute-script dataRequest :switch)]
     (case (count dataRequest)
       1 (p-execute-script :get-switches)
       5 (p-execute-script :get-switches-filter-coords)
       (d/make-data-response
        :error
        nil
        "dataRequest has not the right number of parameters")))))

(defn get-towers
  ([feeder-circuit-operational-id]
   (get-towers feeder-circuit-operational-id []))
  ([feeder-circuit-operational-id coords]
   (let [dataRequest (make-data-request feeder-circuit-operational-id coords)
         p-execute-script (partial execute-script dataRequest :tower)]
     (case (count dataRequest)
       1 (p-execute-script :get-towers)
       5 (p-execute-script :get-towers-filter-coords)
       (d/make-data-response
        :error
        nil
        "dataRequest has not the right number of parameters")))))

(defn get-wires
  ([feeder-circuit-operational-id]
   (get-wires feeder-circuit-operational-id []))
  ([feeder-circuit-operational-id coords]
   (let [dataRequest (make-data-request feeder-circuit-operational-id coords)
         p-execute-script (partial execute-script (into dataRequest coords) :wire)]
     (case (count dataRequest)
       1 (p-execute-script :get-wires)
       5 (p-execute-script :get-wires-filter-coords)
       (d/make-data-response
        :error
        nil
        "dataRequest has not the right number of parameters")))))
