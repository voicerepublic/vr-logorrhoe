(ns vr-logorrhoe.checks
  (:require [vr-logorrhoe.utils :as utils]
            [seesaw.core :as seesaw]
            [clj-http.client :as client]))

;; TODO this endpoint is deprecated use /versions/restream instead, it just returns a number
(def versions-endpoint "https://voicerepublic.com/versions.edn")

;; ------------------------------ connectivity

(def connectivity-warning
  (str "It seems your machine is not connected to the internet. "
       "Please reconnect and press OK."))

(defn- connected? []
  (try (= 200 (:status (client/head versions-endpoint)))
       (catch java.net.SocketException e false)
       (catch java.net.UnknownHostException e false)))

(defn- show-connectivity-warning []
  (println "display connectivity warning")
  (seesaw/alert connectivity-warning))

(defn check-connectivity []
  (while (not (connected?))
    (show-connectivity-warning)))

;; ------------------------------ version

(def client-name "desktop")

; TODO read this from a file
(def client-version 2)

(def update-warning
  (str "Your software is out of date. "
       "Please download and install a recent version "
       "from http://voicerepublic.com"))

(defn- show-update-warning []
  (seesaw/alert update-warning))

(defn check-version []
  (let [versions (-> (client/get versions-endpoint)
                     :body
                     clojure.edn/read-string)
        current (get versions client-name 0)]
    (when (< client-version current)
      (show-update-warning)
      (utils/die "Out of date."))))
