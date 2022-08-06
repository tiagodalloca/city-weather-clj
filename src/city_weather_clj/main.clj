(ns city-weather-clj.main
  (:require [city-weather-clj.system :as system])
  (:gen-class))

(defn -main
  []
  (system/start! "system-config.edn"))

