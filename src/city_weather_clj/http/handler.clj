(ns city-weather-clj.http.handler
  (:require
   [city-weather-clj.api-client.open-weather :refer [get-city-weather]]
   [malli.util :as mu]
   [muuntaja.core :as m]
   reitit.coercion.malli
   [reitit.ring :as ring]
   [reitit.ring.coercion :as coercion]
   reitit.ring.malli
   [reitit.ring.middleware.exception :as exception]
   [reitit.ring.middleware.multipart :as multipart]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]))

(defn get-timestamp
  []
  (let [tz (java.util.TimeZone/getTimeZone "UTC")
        df (new java.text.SimpleDateFormat "yyyy-MM-dd'T'HH:mm:ss'Z'")]
    (.setTimeZone df tz)
    (.format df (new java.util.Date))))

;; TODO: cache responses

(defn get-routes [{weather-api-client :api-client/weather}]
  ["/weather"
   ["/:city"
    {:get
     {:parameters {:path {:city string?}}
      :responses {200 {:body string?}}
      :handler (fn [{{{:keys [city]} :path} :parameters}]
                 (let [{:keys [name]
                        {:keys [temp]} :main}
                       (get-city-weather weather-api-client city)]
                   {:body 
                    {:city name 
                     :datetime (get-timestamp)
                     :temperature temp}}))}}]])

(def options
  {:data
   {:coercion
    (reitit.coercion.malli/create
     {:error-keys
      #{:type :coercion :in :schema :value :errors :humanized :transformed}
      :compile mu/open-schema
      :strip-extra-keys false
      :default-values true
      :options nil})

    :muuntaja m/instance
    :middleware [parameters/parameters-middleware
                 muuntaja/format-negotiate-middleware
                 muuntaja/format-response-middleware
                 (exception/create-exception-middleware
                  (merge
                   exception/default-handlers
                   {clojure.lang.ExceptionInfo
                    (fn [ex _]
                      {:status 500
                       :body (ex-data ex)})
                    ::exception/wrap (fn [handler e request]
                                       (.printStackTrace e)
                                       (handler e request))}))
                 muuntaja/format-request-middleware
                 coercion/coerce-request-middleware
                 multipart/multipart-middleware]}})

(defn get-handler [deps]
  (let [routes (get-routes deps)]
    (ring/ring-handler
     (ring/router routes options))))

