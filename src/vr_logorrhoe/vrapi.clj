(ns vr-logorrhoe.vrapi
  (:require [vr-logorrhoe
             [config :as config :refer [setting state merge-state!]]]
            [taoensso.timbre :as timbre :refer [debug]]
            [clj-http.client :as client]
            [cheshire.core :as json]))

;; ------------------------------ knocking (rest)

;; this can easily be hardcoded, since it will NEVER change
(def ^{:private true} default-endpoint
  "https://voicerepublic.com/api/devices")

(def ^{:private true} loglevel-mapping
  [:debug :info :warn :error :fatal :report])

(defn knock []
  (let [data (-> (str default-endpoint "/" (setting :identifier))
                 (client/get {:accept :json})
                 :body
                 (json/parse-string true))
        log-level (get loglevel-mapping (:loglevel data))]
    (debug "knock returned" data)
    (state :endpoint (:endpoint data))
    (state :log-level log-level)
    (timbre/set-level! log-level)))

;; ------------------------------ registering (rest)

(defn- register-payload []
  {:device
   {:identifier (setting :identifier)
    :type "vr-restream" ;; TODO use a name also used elsewhere
    :subtype "0"}}) ;; TODO use a version/buildnumber also used elsewhere

(defn- register-options []
  {:form-params (register-payload)
   :content-type :json
   :accept :json})

(defn- keywordize
  "Turns an underscored string into a dasherized keyword."
  [string]
  (keyword (clojure.string/replace string #"_" "-")))

(defn register []
  (let [data (-> (client/post (state :endpoint) (register-options))
                 :body
                 (json/parse-string keywordize))]
    (debug "register returned" data)
    (merge-state! data)))
