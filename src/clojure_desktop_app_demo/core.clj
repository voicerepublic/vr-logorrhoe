(ns clojure-desktop-app-demo.core
  (:gen-class)
  ;; (:use seesaw.core)
  (:require [net.n01se.clojure-jna :as jna]
            [clj-http.client :as client]))


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

;; Well written documentation on the Java Sampled Package:
;; http://docs.oracle.com/javase/tutorial/sound/sampled-overview.html
(import '(java.lang Thread))
(import '(java.nio ByteBuffer ShortBuffer channels.FileChannel))
(import '(javax.sound.sampled DataLine AudioSystem LineEvent LineListener AudioFormat))

; Supported audio filetypes
(def filetypes (AudioSystem/getAudioFileTypes))

; Supported mixers
(def mixer-info (seq (. AudioSystem (getMixerInfo))))

; Get mixer-info, name, description of each mixer
(def mixer-info-list
  (map #(let [m %] {:mixer-info m
                    :name (. m (getName))
                    :description (. m (getDescription))}) mixer-info))

;; Create a RAW file format that can be played like this:
;;   aplay -t raw clojure.wav -c 1 -r 44100 -f S16_LE
; -> float sampleRate, int sampleSizeInBits, int channels, boolean signed, boolean bigEndian
(def format (new AudioFormat 44100 16 1 true false))
(def buffer-size (* 22050 2))   ; 44k = 1/2 sec x 2 bytes / sample mono

(comment

;; Get the mixer info for the mic
;; (map #(:name %) mixer-info-list)
(def mic-mixer-info
  (:mixer-info (first (filter #(= "Intel [plughw:0,1]" (:name %)) mixer-info-list))))

;; Get the built in mic mixer
(def mic (. AudioSystem (getMixer mic-mixer-info)))

;; Get the supported source and target lines for the mixer
(def sources (seq (. mic (getSourceLineInfo))))
(def targets (seq (. mic (getTargetLineInfo))))

; Get a target line
(def line-info (first targets))
(def mic-line (try
                (. mic
                   (getLine line-info)
                   (catch Exception e
                     (println "Exception: " e)
                     false))))

;; Cannot get Port.Info to be imported properly. Otherwise this would
;; kinda be the way to check for attributes.
;; (.isLineSupported AudioSystem Port.Info.MICROPHONE)

; Add a line listener for events on the line. This is purely
; optional.
(. mic-line (addLineListener
             (reify LineListener
                (update [this evt]
                  (do (print "Event: " (. evt (getType)))
                      (newline)
                      (. *out* (flush)))))))

; Open the port
(. mic-line (open format buffer-size))

; Start listening to the input
(. mic-line (start))

;; The Input Port will yield a `ByteBuffer`. Saving those cannot just be
;; done with 'spit'. However, they can be saved with a `FileChannel`.
;; http://www.java2s.com/Tutorials/Java/IO/NIO_Buffer/Save_ByteBuffer_to_a_file_in_Java.htm
(def fc (.getChannel (java.io.FileOutputStream. "clojure.wav")))

; try looping and counting available samples
; 1 milli sleep = 1/1000 of a sec = 44 samples
(dotimes [i 100]
  ;; (print "Available data: " (. mic-line (available)))
  ;; (. *out* (flush))
  (let [buffer  (make-array (. Byte TYPE) 2048)
        bcount  (. mic-line (read buffer 0 2048))
        bbyte   (. ByteBuffer (wrap buffer))
        bshort  (. bbyte (asShortBuffer))]
    (.write fc bbyte)
    ;; (print "Read: " bcount " bytes. Buffer state:" (str bshort))
    ;; (print " ... Converted to short: "  (str (. bshort (get 0))))
    )
  (. Thread (sleep 20)))

(.close fc)

; stop the input
;(. mic-line (stop))

; close mic
;(. mic-line (close))


)
