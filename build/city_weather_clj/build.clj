(ns city-weather-clj.build
  (:require [clojure.tools.build.api :as b]))

(def lib 'city-weather-clj)
(def version "0.1.0")
(def class-dir "target/classes")
(def basis (b/create-basis {:project "deps.edn"}))
(def jar-file (format "target/%s-%s.jar" (name lib) version))
(def copy-srcs ["src" "resources"])
(def uber-file (format "target/%s-%s-standalone.jar" (name lib) version))

(defn clean [params]
  (b/delete {:path "target"})
  params)

(defn uber [_]
  (clean nil)
  (b/copy-dir {:src-dirs ["src" "resources"]
               :target-dir class-dir})
  (b/compile-clj {:basis basis
                  :src-dirs ["src"]
                  :class-dir class-dir})
  (b/uber {:class-dir class-dir
           :uber-file uber-file
           :basis basis
           :main 'city-weather-clj.main}))