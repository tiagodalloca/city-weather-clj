(ns city-weather-clj.util)

(defn get-timestamp
  []
  (let [tz (java.util.TimeZone/getTimeZone "UTC")
        df (new java.text.SimpleDateFormat "yyyy-MM-dd'T'HH:mm:ss'Z'")]
    (.setTimeZone df tz)
    (.format df (new java.util.Date))))