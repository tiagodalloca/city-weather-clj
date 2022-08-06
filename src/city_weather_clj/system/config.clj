(ns city-weather-clj.system.config
  (:require [city-weather-clj.http.server :as http-server]
            [city-weather-clj.http.handler :as http-handler]
            [integrant.core :as ig]))

(defmethod ig/init-key :http/server [_ {:keys [handler]
                                        {:keys [port]} :opts}]
  (http-server/start-server {:handler handler :port port}))

(defmethod ig/halt-key! :http/server [_ server]
  (when server (.stop server)))

(defmethod ig/init-key :http/handler [_ _]
  (http-handler/get-handler))
