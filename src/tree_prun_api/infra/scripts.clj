(ns tree-prun-api.infra.scripts
  (:require [clojure.string]))

(defn getScript
  ([typeName]
   [typeName
    (slurp
     (clojure.string/replace
      *file*
      #"scripts.clj"
      (str "scripts/" (name typeName) ".sql")))]))

(def scriptBinds
  [:getpoles
   :getFeederCircuits
   :getPowerTranformers
   :getPowerTranformers
   :getSwitches
   :getTowers
   :getWires])

(def queries  
  (reduce 
   #(try 
      (conj % (getScript %2))
      (catch Exception e 
        (println ";" (.getMessage e))
        %)) {} scriptBinds))







