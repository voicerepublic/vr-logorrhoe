;; Well written tutorial on the Java Sampled Package:
;; http://docs.oracle.com/javase/tutorial/sound/sampled-overview.html

(ns vr-logorrhoe.sound-input
  (:import [java.lang Thread]
           [java.nio ByteBuffer ShortBuffer]
           [javax.sound.sampled DataLine AudioSystem AudioFileFormat$Type
            LineEvent LineListener AudioFormat Port$Info]))


(defn record []
  "hallo")

; Supported audio filetypes
(def filetypes (AudioSystem/getAudioFileTypes))

(def is-wave-supported?
  (AudioSystem/isFileTypeSupported AudioFileFormat$Type/WAVE))

;; Potential recording lines
;; Note: This actually returns an empty list on my Linux. On the Mac,
;; it returns the MIC. The Line-In so far was never returned, even
;; with the USB Interface connected.
(defn get-recording-lines []
  (filter (fn [x]
            (not ( nil? x)))
            (map #(if (AudioSystem/isLineSupported %)
                    %)
                 [Port$Info/LINE_IN Port$Info/MICROPHONE])))

; Supported mixers
(def mixer-info (seq (. AudioSystem (getMixerInfo))))

; Get mixer-info, name, description of each mixer
(def mixer-info-list
  (map #(let [m %] {:mixer-info m
                    :name (. m (getName))
                    :description (. m (getDescription))}) mixer-info))

;; Create a RAW data format. It can be played like this:
;;   aplay -t raw clojure.wav -c 1 -r 44100 -f S16_LE
; -> float sampleRate, int sampleSizeInBits, int channels, boolean signed, boolean bigEndian
(def audio-format (new AudioFormat 44100 16 1 true false))

; 44k = 1/2 sec x 2 bytes / sample mono
(def buffer-size (* 22050 2))

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

; Add a line listener for events on the line. This is purely
; optional.
(. mic-line (addLineListener
             (reify LineListener
                (update [this evt]
                  (do (print "Event: " (. evt (getType)))
                      (newline)
                      (. *out* (flush)))))))

; Open the port
(. mic-line (open audio-format buffer-size))

; Start listening to the input
(. mic-line (start))

;; The Input Port will yield a `ByteBuffer`. Saving those cannot just be
;; done with 'spit'. However, they can be saved with a `FileChannel`.
;; http://www.java2s.com/Tutorials/Java/IO/NIO_Buffer/Save_ByteBuffer_to_a_file_in_Java.htm
(def fc (.getChannel (java.io.FileOutputStream. "clojure.wav")))

;; This should convert audio to a specific format. However, I'm
;; getting an error. This is the doc:
;; http://docs.oracle.com/javase/tutorial/sound/converters.html
;; (def output-file (new java.io.File "clojure.wave"))
;; (.write AudioSystem bbyte AudioFileFormat$Type/WAVE output-file)

; try looping and counting available samples
; 1 milli sleep = 1/1000 of a sec = 44 samples
(dotimes [i 1]
  ;; (print "Available data: " (. mic-line (available)))
  ;; (. *out* (flush))
  (let [tmp-fc  (.getChannel (java.io.FileOutputStream. "tmp.wav"))
        buffer  (make-array (. Byte TYPE) 40480)
        bcount  (. mic-line (read buffer 0 40480))
        bbyte   (. ByteBuffer (wrap buffer))
        bshort  (. bbyte (asShortBuffer))]
    (.write tmp-fc bbyte)



    ;; TODO: THIS IS GOING TO BE A WEIRD ATTEMPT OF LIVE ENCODING
    ;; FRAGMENTS AND SENDING THOSE TO THE ICECAST SERVER. USE WITH
    ;; CAUTION, THIS MIGHT NOT WORK.
    ;; If it does, however, it will save us from having to import a
    ;; mp3 encoding lib that supports streaming



    (.close tmp-fc)
    (clojure.java.shell/sh "sh" "-c" "lame -r -s 44.1 --bitwidth 16 --signed --little-endian -m m tmp.wav foo.mp3")
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
