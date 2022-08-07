(ns city-weather-clj.http.handler.tests
  #_{:clj-kondo/ignore [:use]}
  (:use [clojure.test])
  (:require [city-weather-clj.api-client.open-weather :refer [OpenWeatherClient]]
            [city-weather-clj.cache :refer [make-weather-cache] :as cache-ns]
            [city-weather-clj.http.handler :refer [get-city-weather-handler]]
            [city-weather-clj.util :refer [add-minutes-to-date get-timestamp
                                           now-date]]))

(defrecord MockOpenWeatherClient [fn-city->response]
  OpenWeatherClient
  (get-city-weather
    [_ city]
    (fn-city->response city)))

(def city->temp
  {"campinas" 27.9})

(def weather-api-client-mock
  (->MockOpenWeatherClient
   (fn [city]
     {:name city
      :main {:temp (get city->temp (.toLowerCase city))}})))

(deftest get-city-weather-handler-test
  (let [cache (make-weather-cache)
        current-date (now-date)
        current-date-plus-5min (add-minutes-to-date current-date 5)
        current-date-plus-6min (add-minutes-to-date current-date 6)
        deps {:api-client/weather weather-api-client-mock
              :system/cache cache}]
    (with-redefs [now-date (constantly current-date)]
      (testing "Without cache"
        (is (= (-> (get-city-weather-handler
                    {:parameters {:path {:city "Campinas"}}} deps)
                   :body)
               {:city "Campinas"
                :temperature (get city->temp "campinas")
                :datetime (get-timestamp current-date)}))))
    
    (with-redefs [now-date
                  ;; cache should be used if time diff <= 5min
                  (constantly current-date-plus-5min)]
      (testing "With cache"
        (is (= (-> (get-city-weather-handler
                    {:parameters {:path {:city "Campinas"}}} deps)
                   :body)
               {:city "Campinas"
                :temperature (get city->temp "campinas")
                ;; timestamp from cache should be returned
                :datetime (get-timestamp current-date)}))))
    
    (with-redefs [now-date
                  ;; cache should be updated if time diff > 5min
                  (constantly current-date-plus-6min)]
      (testing "Updating cache"
        (is (= (-> (get-city-weather-handler
                    {:parameters {:path {:city "Campinas"}}} deps)
                   :body)
               {:city "Campinas"
                :temperature (get city->temp "campinas")
                ;; new timestamp should be used
                :datetime (get-timestamp current-date-plus-6min)}))
        ;; was cache updated? it should've been
        (is (= (-> (cache-ns/get-city-weather cache "campinas")
                   :datetime)
               (get-timestamp current-date-plus-6min)))))))

(comment
  (get-city-weather-handler-test))