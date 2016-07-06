(ns vr-logorrhoe.shout
  (:require
   [vr-logorrhoe
    [config :as config]]
   [clj-http.client :as client]))

(defn- stream-endpoint []
  (str "http://"
       (get-in (config/state :venue) [:icecast :public-ip-address])
       ":"
       (get-in (config/state :venue) [:icecast :port])
       "/"
       (get-in (config/state :venue) [:icecast :mount-point])))

(defn stream [input-stream]
  (client/put (stream-endpoint)
              {
               :basic-auth ["source" (get-in (config/state :venue)
                                             [:icecast :source-password])]
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
