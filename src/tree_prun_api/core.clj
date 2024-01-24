(ns tree-prun-api.core
  (:require [tree-prun-api.infra.repository :as r :refer [->GisRepository]])
  (:gen-class))



(def rGis (->GisRepository))

(comment
  ;;;;some tests
  (.getPoles rGis "AnythingForNow")
  (.getFeederCircuits rGis "AnythingForNow"))

