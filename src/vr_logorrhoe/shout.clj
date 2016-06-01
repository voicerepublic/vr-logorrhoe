(ns vr-logorrhoe.shout
  (:require [clj-http.client :as client]))

(def shout-config (atom {:host "52.29.75.26"
                         :port 80
                         :password "sxoneagh"
                         :mount "9da13579-bcb2-45fc-8254-0c8364bfcca2"}))

(defn stream [input-stream]
  (client/put (str "http://" (:host @shout-config) "/" (:mount @shout-config))
              {
               :basic-auth ["source" (:password @shout-config)]
               :multipart [{:name "/foo"
                            :content input-stream
                            :length -1}]
               :headers {
                         ;; :user-agent "vr_shout/0.2.0"
                         ;; :ice-bitrate "256"
                         ;; :content-type "application/ogg"
                         :content-type "audio/mpeg"
                         ;; :ice-name "VR Server Name"
                         ;; :ice-genre "Rock"
                         ;; :ice-title "VR Title"
                         ;; :ice-url "https://voicerepublic.com"
                         ;; :ice-private "0"
                         ;; :ice-public "1"
                         ;; :ice-description "VR Server Description"
                         ;; :ice-audio-info "ice-samplerate=44100;ice-bitrate=256;ice-channels=1"
                         }
               }))
