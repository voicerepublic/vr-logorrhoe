(ns vr-logorrhoe.shout
  (:require
   [vr-logorrhoe
    [config :as config]]
   [clj-http.client :as client]))

(def shout-config (atom {:host "52.59.7.22"
                         :port 80
                         :password "vdzqlugn"
                         :mount "e7117be6-3c09-42ae-8ce1-78dce2a6e347"}))

(defn- stream-endpoint []
  (str "http://" (:host @shout-config) "/" (:mount @shout-config)))

(defn stream [input-stream]
  (client/put (stream-endpoint)
              {
               :basic-auth ["source" (:password @shout-config)]
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
