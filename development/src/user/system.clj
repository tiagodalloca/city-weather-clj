(ns user.system
  #_{:clj-kondo/ignore [:unused-referred-var]}
  (:require [city-weather-clj.http.server :as http-server]
            [city-weather-clj.http.handler :as http-handler]
            [city-weather-clj.api-client.open-weather :refer [make-open-weather-client]]
            [integrant.core :as ig]
            [integrant.repl :refer [clear go halt init prep reset reset-all]]))

(def config
  {:http/server {:opts {:port 8989}
                 :handler (ig/ref :http/handler)}

   :http/handler {:api-client/weather (ig/ref :api-client/weather)}
   :api-client/weather {:endpoint "https://api.openweathermap.org/data/2.5/weather"
                        :api-key "API_KEY"}})

(integrant.repl/set-prep! (constantly config))

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
  (make-open-weather-client endpoint api-key))

(comment
  (prep)
  (init))