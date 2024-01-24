(ns tree-prun-api.infra.scripts
  (:require [clojure.string]
            [clojure.java.io])
  (:gen-class))

(defn getScript
  ([typeName]
   [typeName
    (slurp 
     (-> 
      (clojure.java.io/file 
       (clojure.java.io/resource "infra") 
       "scripts" 
        (str (name typeName) ".sql")) 
      .getPath))]))

(def scriptBinds
  [:getPoles
   :getPolesFilterCoords

   :getFeederCircuits

   :getPowerTranformers
   :getPowerTranformersFilterCoords

   :getSwitches
   :getSwitchesFilterCoords

   :getTowers
   :getTowersFilterCoords

   :getWires
   :getWiresFilterCoords
   
   :insertPole])

(def scripts  
  (reduce
   #(try
      (conj % (getScript %2))
      (catch Exception e
        (println ";" (.getMessage e))
        %))
   {} scriptBinds))







