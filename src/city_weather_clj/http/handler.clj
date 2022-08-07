(ns city-weather-clj.http.handler
  (:require
   [city-weather-clj.api-client.open-weather :as open-weather-client]
   [city-weather-clj.cache :as cache]
   [city-weather-clj.util :refer [get-timestamp date-from-timestamp diff-minutes-dates]]
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

(defn get-city-weather-handler
  [{{{:keys [city]} :path} :parameters}
   {weather-api-client :api-client/weather
    system-cache :system/cache}]
  (try
    (let [{cache-datime :datetime
           :as cache-city-weather}
          (cache/get-city-weather system-cache city)

          cache-date (some-> cache-datime date-from-timestamp)
          cache-diff (some->> cache-date (diff-minutes-dates (Date.)))
          is-cache-outdated (or (not (some? cache-diff))
                                (> cache-diff 5))

          city-weather
          (if is-cache-outdated
            (let [{:keys [name]
                   {:keys [temp]} :main}
                  (open-weather-client/get-city-weather weather-api-client city)]
              {:city name
               :datetime (get-timestamp)
               :temperature temp})
            (do
              (println (str "Retrieving from cache for city " city))
              cache-city-weather))]
      (when is-cache-outdated
        (println (str "Updating cache " city-weather))
        (cache/assoc-city-weather! system-cache city-weather))
      {:body city-weather})
      (catch Exception e
        {:status 500 :body (str "Couldn't find weather information for this city.")})))

(defn inject-handler-deps
  [handler deps]
  (fn [request]
    (handler request deps)))

(defn get-routes
  [deps]
  ["/weather"
   ["/:city"
    {:get
     {:parameters {:path {:city string?}}
      :responses {200 {:body string?}}
      :handler (inject-handler-deps
                get-city-weather-handler
                deps)}}]])

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

(defn get-handler
  [deps]
  (let [routes (get-routes deps)]
    (ring/ring-handler
     (ring/router routes options)
     (constantly {:status 404, :body "Not found."}))))

