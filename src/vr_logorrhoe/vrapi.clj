(ns vr-logorrhoe.vrapi
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]))


(defn uuid []
  (str (java.util.UUID/randomUUID)))

;; TODO needs to come from settings
(def identifier (uuid))

(def default-endpoint
  "https://voicerepublic.com/api/devices")

(defn knock []
  (let [data (-> (str default-endpoint "/" identifier)
                 client/get
                 :body
                 (json/read-str :key-fn keyword))]
    ;; TOOD needs to be stored in settings
    (prn data)))
