(ns vr-logorrhoe.checks
  (:require [seesaw.core :as seesaw]
            [clj-http.client :as client]))


(def versions-endpoint "https://voicerepublic.com/versions.edn")

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

;;(def client-name "desktop")
;;
;;(defn check-version []
;;  (let [versions (-> (client/get versions-endpoint)
;;                  :body
;;                  clojure.edn/read-string)]
;;    versions))
