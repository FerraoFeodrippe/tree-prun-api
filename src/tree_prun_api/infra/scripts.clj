(ns tree-prun-api.infra.scripts
  (:require [clojure.string])
  (:gen-class))

(defn getScript
  ([typeName]
   [typeName
    (slurp
     (clojure.string/replace
      *file*
      #"scripts.clj"
      (str "scripts/" (name typeName) ".sql")))]))

(def scriptBinds
  [:getPoles
   :getFeederCircuits
   :getPowerTranformers
   :getPowerTranformers
   :getSwitches
   :getTowers
   :getWires
   
   :insertPole])

(def scripts  
  (reduce
   #(try
      (conj % (getScript %2))
      (catch Exception e
        (println ";" (.getMessage e))
        %))
   {} scriptBinds))







