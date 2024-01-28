(ns tree-prun-api.infra.scripts
  (:require [clojure.string]
            [clojure.java.io])
  (:gen-class))

(defn get-script
  ([typeName]
   [typeName
    (slurp 
     (-> 
      (clojure.java.io/file 
       (clojure.java.io/resource "infra") 
       "scripts" 
        (str (name typeName) ".sql")) 
      .getPath))]))

(def script-binds
 {:gis
  [:get-poles
   :get-poles-filter-coords

   :get-feeder-circuits

   :get-power-tranformers
   :get-power-tranformers-filter-coords

   :get-switches
   :get-switches-filter-coords

   :get-towers
   :get-towers-filter-coords

   :get-wires
   :get-wires-filter-coords

   :insert-pole]
  
  :tree-prun
  [:get-service-orders]})

(def scripts-binds-from
   (->> script-binds
        (reduce
         #(into %
                (reduce
                 (fn [a b]
                   (assoc a b (first %2)))
                 {}
                 (second %2)))
         {})))

 (def scripts
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
        {})))



