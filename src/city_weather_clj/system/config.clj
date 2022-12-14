(ns city-weather-clj.system.config
  (:require [city-weather-clj.http.server :as http-server]
            [city-weather-clj.http.handler :as http-handler]
            [city-weather-clj.api-client.open-weather :refer [make-open-weather-client]]
            [city-weather-clj.cache :refer [make-weather-cache]]
            [integrant.core :as ig]))

(defmethod ig/init-key :http/server [_ {:keys [handler]
                                        {:keys [port]} :opts}]
  (http-server/start-server {:handler handler :port port}))

(defmethod ig/halt-key! :http/server [_ server]
  (when server (.stop server)))

#_{:clj-kondo/ignore [:unused-binding]}
(defmethod ig/init-key :http/handler [_ deps]
  (http-handler/get-handler deps))

(defmethod ig/init-key :api-client/weather
  [_ {:keys [endpoint api-key]}] 
  (when-not (some? api-key)
    (throw (ex-info "Missing api-key" {:api-key api-key})))
  (make-open-weather-client endpoint api-key))

(defmethod ig/init-key :system/cache
  [_ _]
  (make-weather-cache))
