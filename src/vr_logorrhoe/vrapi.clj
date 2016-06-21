(ns vr-logorrhoe.vrapi
  (:require [clj-http.client :as client]
            [cheshire.core :as json]))


;; TODO move to utils
(defn uuid []
  (str (java.util.UUID/randomUUID)))

;; TODO needs to come from settings
(def identifier (uuid))

;; ------------------------------ knocking

(def default-endpoint
  ;;"https://voicerepublic.com/api/devices")
  "http://localhost:3000/api/devices")

(defn knock []
  (let [data (-> (str default-endpoint "/" identifier)
                 (client/get {:accept :json})
                 :body
                 (json/parse-string true))]
    ;; TOOD needs to be stored in settings
    (prn data)))

;; ------------------------------ registering

(defn- register-payload []
  {:device
   {:identifier identifier
    :type "vr-restream" ;; TODO use a name also used elsewhere
    :subtype "0"}}) ;; TODO use a version/buildnumber also used elsewhere

(defn- register-options []
  {:form-params (register-payload)
   :content-type :json
   :accept :json
   :debug true
   :throw-entire-message? true})

(defn register []
  ;; TODO use enpoint from settings instead of default-endpoint
  (let [data (-> (client/post default-endpoint (register-options))
                 :body
                 (json/parse-string true))]
    ;; TODO needs to be stored in settings
    (prn data)))
