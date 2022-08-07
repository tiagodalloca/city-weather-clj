(ns user.system
  #_{:clj-kondo/ignore [:unused-referred-var]}
  (:require [city-weather-clj.system]
            [integrant.core :as ig]
            [integrant.repl :refer [clear go halt init prep reset reset-all]]))

(def config
  {:http/server {:opts {:port 8989}
                 :handler (ig/ref :http/handler)}

   :http/handler {:api-client/weather (ig/ref :api-client/weather)}
   :api-client/weather {:endpoint "https://api.openweathermap.org/data/2.5/weather"
                        :api-key "API_KEY"}})

(integrant.repl/set-prep! (constantly config))

(comment
  (prep)
  (init))