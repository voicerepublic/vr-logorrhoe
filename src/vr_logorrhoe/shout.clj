(ns vr-logorrhoe.shout
  (:require
   [vr-logorrhoe
    [config :as config]]
   [clj-http.client :as client]))

(defn shout-config [param]
  "Retrieves the specified parameter from the global configuration"
  (param @config/settings))

(defn- stream-endpoint []
  (str "http://" (shout-config :host) "/" (shout-config :mountpoint)))

(defn set-host [host]
  (config/update-setting :host host))

(defn set-password [host]
  (config/update-setting :password host))

(defn set-mountpoint [host]
  (config/update-setting :mountpoint host))

;; TODO: Actually use the port
(defn set-port [host]
  (config/update-setting :port host))

(defn stream [input-stream]
  (client/put (stream-endpoint)
              {
               :basic-auth ["source" (shout-config :password)]
               :multipart [{:name "/foo"
                            :content input-stream
                            :length -1}]
               :headers {
                         :ice-bitrate "256"
                         :content-type "audio/mpeg"
                         :ice-audio-info (str "ice-samplerate="
                                              (:sample-freq @config/settings)
                                              ";ice-bitrate=256;ice-channels="
                                              (:audio-channels @config/settings))
                         :user-agent "vr_shout/0.2.0"
                         ;; :content-type "application/ogg"
                         :ice-name "VR Server Name"
                         ;; :ice-genre "Rock"
                         :ice-title "VR Title"
                         :ice-url "https://voicerepublic.com"
                         ;; :ice-private "0"
                         ;; :ice-public "1"
                         :ice-description "VR Server Description"
                         }
               }))
