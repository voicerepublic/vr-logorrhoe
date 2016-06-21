(ns vr-logorrhoe.shout
  (:require
   [vr-logorrhoe
    [config :as config]]
   [clj-http.client :as client]))

(defn- stream-endpoint []
  (str "http://" (config/setting :host) "/" (config/setting :mountpoint)))

(defn set-host [host]
  (config/setting :host host))

(defn set-password [host]
  (config/setting :password host))

(defn set-mountpoint [host]
  (config/setting :mountpoint host))

;; TODO: Actually use the port
(defn set-port [host]
  (config/setting :port host))

(defn stream [input-stream]
  (client/put (stream-endpoint)
              {
               :basic-auth ["source" (config/setting :password)]
               :multipart [{:name "/foo"
                            :content input-stream
                            :length -1}]
               :headers {
                         :ice-bitrate "256"
                         :content-type "audio/mpeg"
                         :ice-audio-info (str "ice-samplerate="
                                              (config/setting :sample-freq)
                                              ";ice-bitrate=256;ice-channels="
                                              (config/setting :audio-channels))
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
