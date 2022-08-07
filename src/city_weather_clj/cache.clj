(ns city-weather-clj.cache)

(defprotocol StatefulWeatherCache
  (get-city-weather [this city])
  (assoc-city-weather! [this city-weather]))

(defrecord AtomWeatherCache [map-atom]
  StatefulWeatherCache
  (get-city-weather
    [_ city]
    (get @map-atom (.toUpperCase city)))
  (assoc-city-weather!
    [_ {:keys [city] :as city-weather}]
    (swap! map-atom assoc (.toUpperCase city) city-weather)))

(defn make-weather-cache
  []
  ;; hash-map is used becasue it is more efficient than
  ;; array-map for larger counts entries
  (->AtomWeatherCache (atom (hash-map))))
