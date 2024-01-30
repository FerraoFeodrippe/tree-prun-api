(ns tree-prun-api.config
  (:require [clojure.java.io :as io]
            [next.jdbc :as jdbc]))

(defn- add-config-db-env
  [config env]
  (-> config
      (assoc-in
       [:gis :db]
       (env {:dev {:dbtype "sqlite" :dbname "tree_prun_test.db"}
             :prod {:dbtype "sqlite" :dbname "tree_prun.db"}}))
      ;;in this case contextx :gis and :tree-prun have same db, but could be different
      (assoc-in
       [:tree-prun :db]
       (env {:dev {:dbtype "sqlite" :dbname "tree_prun_test.db"}
             :prod {:dbtype "sqlite" :dbname "tree_prun.db"}}))))

(defn- add-config-script-binds
  [config]
  (-> config
      (assoc-in
       [:gis :script-binds]
       [:get-poles
        :get-poles-filter-coords
        :insert-pole

        :get-feeder-circuits

        :get-power-tranformers
        :get-power-tranformers-filter-coords

        :get-switches
        :get-switches-filter-coords

        :get-towers
        :get-towers-filter-coords

        :get-wires
        :get-wires-filter-coords])

      (assoc-in
       [:tree-prun :script-binds]
       [:get-service-orders
        :insert-service-order

        :get-teams

        :get-operational-bases

        :get-trees-pruning
        :insert-tree-pruning])))

(defn make-config
  [env]
  (-> {}
      (add-config-db-env env)
      add-config-script-binds))

(defn- get-script
  ([typeName]
   [typeName
    (slurp
     (->
      (io/file
       (io/resource "infra")
       "scripts"
       (str (name typeName) ".sql"))
      .getPath))]))

(defn- make-scripts
  [config]
  (let [contexts (map first config)
        script-binds (reduce #(into % {%2 (-> config %2 :script-binds)}) {} contexts)]
    (->> script-binds
         (reduce
          #(into % (second %2))
          [])
         (reduce
          #(try
             (conj % (get-script %2))
             (catch Exception e
               (println ";" (.getMessage e))
               %))
          {}))))

(defn- make-scripts-binds-from
  [config]
  (let [contexts (map first config)
        script-binds (reduce #(into % {%2 (-> config %2 :script-binds)}) {} contexts)]
    (->> script-binds
         (reduce
          #(into %
                 (reduce
                  (fn [a b]
                    (assoc a b (first %2)))
                  {}
                  (second %2)))
          {}))))

(defn make-system
  [{:keys [config]}]
  {:ds {:gis (jdbc/get-datasource (-> config :gis :db))
        :tree-prun (jdbc/get-datasource (-> config :tree-prun :db))}
   :config config
   :scripts (make-scripts config)
   :scripts-binds-from (make-scripts-binds-from config)
   :server (atom nil)})

#_(make-config :dev)
#_(-> (make-config :dev) :tree-prun :db)
#_(-> (make-config :dev) :gis :db)
#_(jdbc/get-datasource (-> (make-config :dev) :gis :db))

#_(jdbc/get-datasource (-> (make-config :dev) :tree-prun :db))
#_(make-system {:config (make-config :dev) 
                :server-handler {}})


