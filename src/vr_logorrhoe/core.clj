(ns vr-logorrhoe.core
  (:gen-class)
  ;; (:use seesaw.core)
  (:require [net.n01se.clojure-jna :as jna]
            [clj-http.client :as client]
            [vr-logorrhoe.audio :refer [record]]))


;; (defn crazy-batshit-from-c [num]
;;   (jna/invoke Integer c/printf "The number given: %d\n" num))


;; (defn -main [& args]
;;   (native!)
;;   (invoke-later
;;     (-> (frame :title "Hello VR",
;;            :content "Hello, VR - this is from Clojure!",
;;            :on-close :exit)
;;      pack!
;;      show!)))

;; (import java.util.Stack)
;; (Stack.)

;; (client/put "http://52.58.65.224/4609"
;;              {
;;               :basic-auth ["source" "thisisagoodpassword"]
;;               :body (clojure.java.io/file "/home/munen/src/voicerepublic_icecast_tests/manual_put/test.ogg")
;;               :headers {
;;                         :user-agent "vr_shout/0.2.0"
;;                         :ice-bitrate "128"
;;                         :content-type "application/ogg"
;;                         :ice-name "VR Server Name"
;;                         :ice-genre "Rock"
;;                         :ice-title "VR Title"
;;                         :ice-url "https://voicerepublic.com"
;;                         :ice-private "0"
;;                         :ice-public "1"
;;                         :ice-description "VR Server Description"
;;                         :ice-audio-info "ice-samplerate=44100;ice-bitrate=128;ice-channels=2"
;;                         }
;;               })


;; (-main)
;; (crazy-batshit-from-c 112423)
