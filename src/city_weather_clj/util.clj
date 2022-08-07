(ns city-weather-clj.util
  (:import (javax.xml.bind DatatypeConverter)
           (java.util.concurrent TimeUnit)))

(defn get-timestamp
  []
  (let [tz (java.util.TimeZone/getTimeZone "UTC")
        df (new java.text.SimpleDateFormat "yyyy-MM-dd'T'HH:mm:ss'Z'")]
    (.setTimeZone df tz)
    (.format df (new java.util.Date))))

(defn date-from-timestamp
  [timestamp]
  (.getTime (DatatypeConverter/parseDateTime timestamp)))

(defn diff-minutes-dates
  [d1 d2]
  (let [diff (- (.getTime d2) (.getTime d1))]
    (.convert TimeUnit/MINUTES diff (TimeUnit/MILLISECONDS))))
