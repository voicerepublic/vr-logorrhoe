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
    ;; TODO this could be handled somewhere else where it belongs by
    ;; watching the state
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

;; ------------------------------ polling (rest)

(defn- polling-endpoint []
  ;; https://voicerepublic.com/polling/devices/:identifier
  (str (state :polling-endpoint) "/" (setting :identifier)))

(defn- polling-options []
  {})

(defn- poll []
  ;; Since the poll also replaces the heartbeat it is a HTTP PUT.
  (let [data (-> (client/put (polling-endpoint) (polling-options))
                 :body
                 (json/parse-string keywordize))]
    (debug "poll returned" data)
    (merge-state! data)))

(defn- polling-interval []
  (* 1000 (state :heartbeat-interval)))

(defn- while-call-throttled [test ms f & args]
  (future
    (while (test)
      (do
        (apply f args)
        (Thread/sleep ms)))))

(defn start-polling []
  (state :polling true)
  (while-call-throttled #(state :polling) (polling-interval) poll))

(defn stop-polling []
  (state :polling false))
