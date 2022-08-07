(ns city-weather-clj.util
  (:import (javax.xml.bind DatatypeConverter)
           (java.util.concurrent TimeUnit)
           (java.util Date Calendar)))
(defn now-date
  []
  (Date.))

(defn get-timestamp
  ([date]
   (let [tz (java.util.TimeZone/getTimeZone "UTC")
         df (new java.text.SimpleDateFormat "yyyy-MM-dd'T'HH:mm:ss'Z'")]
     (.setTimeZone df tz)
     (.format df date)))
  ([] (get-timestamp (now-date))))

(defn add-minutes-to-date
  [date minutes]
  (let [c (Calendar/getInstance)]
    (doto c
      (.setTime date)
      (.add Calendar/MINUTE minutes))
    (.getTime c)))

(defn date-from-timestamp
  [timestamp]
  (.getTime (DatatypeConverter/parseDateTime timestamp)))

(defn diff-minutes-dates
  [d1 d2]
  (let [diff (- (.getTime d2) (.getTime d1))]
    (.convert TimeUnit/MINUTES diff (TimeUnit/MILLISECONDS))))
