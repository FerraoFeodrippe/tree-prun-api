(ns tree-prun-api.configurations
  (:require [next.jdbc :as jdbc])
  (:gen-class))

(def sqlite-db {:dbtype "sqlite" :dbname "tree_prun.db"})

(def map-ds
  {:gis (jdbc/get-datasource sqlite-db)})
