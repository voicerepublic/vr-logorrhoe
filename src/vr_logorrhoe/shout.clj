(ns vr-logorrhoe.shout
  (:require
   [vr-logorrhoe
    [config :as config]]
   [clj-http.client :as client]))

(def shout-config (atom {:host "52.58.150.45"
                         :port 80
                         :password "pwogaeyd"
                         :mount "5fa60d02-991c-44c4-9882-656522faa605"}))

(defn stream [input-stream]
  (client/put (str "http://" (:host @shout-config) "/" (:mount @shout-config))
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
                         ;; :user-agent "vr_shout/0.2.0"
                         ;; :content-type "application/ogg"
                         ;; :ice-name "VR Server Name"
                         ;; :ice-genre "Rock"
                         ;; :ice-title "VR Title"
                         ;; :ice-url "https://voicerepublic.com"
                         ;; :ice-private "0"
                         ;; :ice-public "1"
                         ;; :ice-description "VR Server Description"
                         }
               }))
