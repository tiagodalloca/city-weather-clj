(ns city-weather-clj.system
  (:require [city-weather-clj.system.config]
            [clojure.java.io :as io]
            [integrant.core :as ig]
            [aero.core :as aero]))

(defmethod aero/reader 'ig/ref
  [_ _ value]
  (ig/ref value))

(defmethod aero/reader 'io/resource
  [_ _ value]
  (try 
    (slurp (io/resource value))
    (catch Exception _
      (println "Couldn't read resource \"" value \"))))

(defn read-classpath-config
  [file-name]
  (aero/read-config (io/resource file-name)))

(comment
  (read-classpath-config "system-config.edn"))

(defonce state (atom nil))

(defn start!
  [config-file]
  (let [config (read-classpath-config config-file)]
    (reset!
     state
     (-> (ig/prep config)
         (ig/init)))))

(defn stop!
  []
  (reset! state (ig/halt! @state)))
