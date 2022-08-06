(ns user.system
  #_{:clj-kondo/ignore [:unused-referred-var]}
  (:require [city-weather-clj.http.server :as http-server]
            [city-weather-clj.http.handler :as http-handler]
            [integrant.core :as ig]
            [integrant.repl :refer [clear go halt init prep reset reset-all]]))

(def config
  {::server {:opts {:port 8989}
             :handler (ig/ref ::handler)}

   ::handler {}})

 (integrant.repl/set-prep! (constantly config))

(defmethod ig/init-key ::server [_ {:keys [handler]
                                    {:keys [port]} :opts}]
  (http-server/start-server {:handler handler :port port}))

 (defmethod ig/halt-key! ::server [_ server]
   (when server (.stop server)))

#_{:clj-kondo/ignore [:unused-binding]}
(defmethod ig/init-key ::handler [_ deps]
  (http-handler/get-handler))

