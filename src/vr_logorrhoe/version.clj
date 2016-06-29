(ns vr-logorrhoe.version
  (:require [clojure.java.io :as io])
  (:import (java.util Properties)))

;; http://stackoverflow.com/questions/11235445

(defn- version-from-system-properties []
  (System/getProperty "vr-logorrhoe.version"))

(defn- version-from-pom-properties [groupid artifact]
  (-> (doto (Properties.)
        (.load (-> "META-INF/maven/%s/%s/pom.properties"
                   (format groupid artifact)
                   (io/resource)
                   (io/reader))))
      (.get "version")))

;; http://www.javapractices.com/topic/TopicAction.do?Id=238

(deftype Dummy [])

(defn- version-from-package-via-deftype []
  (.. (->Dummy) getClass getPackage getSpecificationVersion))


;;(defn- version-from-package-via-gen-class []
;;  (let [object (new (gen-class :name "Dummy"))]
;;    (.. object getClass getPackage getSpecificationVersion)))


(defn survey-version-methods []
  (println (try
             (version-from-system-properties)
             (catch Exception e (str "caught exception: " (.getMessage e)))))

  (println (try
             (version-from-pom-properties "a" "b")
             (catch Exception e (str "caught exception: " (.getMessage e)))))

  (println (try
             (version-from-package-via-deftype)
             (catch Exception e (str "caught exception: " (.getMessage e)))))

  ;;(println (try
  ;;           (version-from-system-via-gen-class)
  ;;           (catch Exception e (str "caught exception: " (.getMessage e)))))

  (println "Done."))

(survey-version-methods)


(defn version []
  (or (version-from-system-properties)
      (version-from-package-via-dummy)
      (version-from-package-via-gen-class)
      (version-from-pom-properties "a" "b"))) ; TODO set groupid & artifact
