(ns city-weather-clj.api-client.open-weather
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]))

(defprotocol OpenWeatherClient
  (get-city-weather [this city]))

(defrecord OpenWeatherRecordClient [endpoint api-key]
  OpenWeatherClient
  (get-city-weather
    [_ city]
    (-> (client/get endpoint {:query-params {"q" city
                                         "appid" api-key
                                         "units" "metric"}})
        :body
        (json/read-str :key-fn keyword))))

(defn make-open-weather-client
  [endpoint api-key]
  (->OpenWeatherRecordClient endpoint api-key))

(comment
  (def weather-client (make-open-weather-client 
                       "https://api.openweathermap.org/data/2.5/weather"
                       "API_KEY"))
  (get-city-weather weather-client "Campinas")
  )
