;; Well written tutorial on the Java Sampled Package:
;; http://docs.oracle.com/javase/tutorial/sound/sampled-overview.html

(ns vr-logorrhoe.sound-input
  (:import [java.lang Thread]
           [java.nio ByteBuffer ShortBuffer]
           [javax.sound.sampled DataLine AudioSystem AudioFileFormat$Type
            LineEvent LineListener AudioFormat Port$Info]))


(comment
  ;; Supported audio filetypes
  (def filetypes (AudioSystem/getAudioFileTypes))

  (def is-wave-supported?
    (AudioSystem/isFileTypeSupported AudioFileFormat$Type/WAVE))
  )

;; Supported mixers
;; Note: It would be possible to check for LINE_IN and MICROPHONE
;; directly using
;; (AudioSystem/isLineSupported Port$Info/LINE_IN)
;; and
;; (AudioSystem/getLine Port$Info/LINE_IN)
;; However, this does not always return a line with said capability
;; (seen in Debian 8). Therefore, we're using the method to request a
;; line via the Mixer.
(defn get-mixer-info []
  (seq (. AudioSystem (getMixerInfo))))


;; Get mixer-info, name, description of each mixer
(defn get-mixer-infos []
  (map #(let [m %] {:mixer-info m
                    :name (. m (getName))
                    :description (. m (getDescription))})
       (get-mixer-info)))

;; This method is called by the GUI to query for available mixers
(defn get-mixer-names []
  "Retrieve all available mixers by name"
  (map :name (get-mixer-infos)))

;; Get the mixer info for the recorder
(defn get-recorder-mixer-info [recorder-name]
  (:mixer-info (first (filter #(= recorder-name (:name %)) (get-mixer-infos)))))

;; TODO: !! This is hard-coded for my machine right now. !!
(def recorder-mixer-info
  (get-recorder-mixer-info "Intel [plughw:0,1]"))

;; Get the recorder mixer
(def recorder-mixer (. AudioSystem (getMixer recorder-mixer-info)))

;; Get the supported target line for the mixer
(def line-info (first (seq (. recorder-mixer (getTargetLineInfo)))))

; Get a target line
(def recorder-line (try
                (. recorder-mixer
                   (getLine line-info)
                   (catch Exception e
                     (println "Exception with getting the line for mixer: " e)
                     false))))

;; Add a line listener for events on the line. This is an optional step
;; and will be purely used for logging purposes.
(. recorder-line (addLineListener
             (reify LineListener
                (update [this evt]
                  (do (print "Event: " (. evt (getType)))
                      (newline)
                      (. *out* (flush)))))))

;; Create a RAW data format. It can be played like this:
;;   aplay -t raw clojure.wav -c 1 -r 44100 -f S16_LE
;; -> float sampleRate, int sampleSizeInBits, int channels, boolean signed, boolean bigEndian
(def audio-format (new AudioFormat 44100 16 1 true false))

;; 44k = 1/2 sec x 2 bytes / sample mono
(def buffer-size (* 22050 2))


(defn record[]

  ;; Open the port
  (. recorder-line (open audio-format buffer-size))

                                        ; Start listening to the input
  (. recorder-line (start))

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
  (dotimes [i 100]
    ;; (print "Available data: " (. recorder-line (available)))
    ;; (. *out* (flush))
    (let [;tmp-fc  (.getChannel (java.io.FileOutputStream. "tmp.wav"))
          buffer  (make-array (. Byte TYPE) 4048)
          bcount  (. recorder-line (read buffer 0 4048))
          bbyte   (. ByteBuffer (wrap buffer))
          bshort  (. bbyte (asShortBuffer))]

      (.write fc bbyte)


      ;; (.write tmp-fc bbyte)



      ;; TODO: THIS IS GOING TO BE A WEIRD ATTEMPT OF LIVE ENCODING
      ;; FRAGMENTS AND SENDING THOSE TO THE ICECAST SERVER. USE WITH
      ;; CAUTION, THIS MIGHT NOT WORK.
      ;; If it does, however, it will save us from having to import a
      ;; mp3 encoding lib that supports streaming



      ;; (.close tmp-fc)
      ;; (clojure.java.shell/sh "sh" "-c" "lame -r -s 44.1 --bitwidth 16 --signed --little-endian -m m tmp.wav foo.mp3")
      ;; (print "Read: " bcount " bytes. Buffer state:" (str bshort))
      ;; (print " ... Converted to short: "  (str (. bshort (get 0))))
      )
    (. Thread (sleep 20)))

  (.close fc)

  ;; stop the input
  (. recorder-line (stop))

  ;; close recorder
  (. recorder-line (close)))



;; (record)


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
