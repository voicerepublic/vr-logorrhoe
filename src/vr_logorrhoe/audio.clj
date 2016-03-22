;; Well written tutorial on the Java Sampled Package:
;; http://docs.oracle.com/javase/tutorial/sound/sampled-overview.html

(ns vr-logorrhoe.audio
  (:import [java.lang Thread]
          [java.nio ByteBuffer ShortBuffer]
          [javax.sound.sampled DataLine AudioSystem LineEvent LineListener AudioFormat Port$Info]))

(defn record []
  "hallo")


; Supported audio filetypes
(def filetypes (AudioSystem/getAudioFileTypes))

;; Potential recording lines
;; Note: This actually returns an empty list on my Linux. On the Mac,
;; it returns the MIC. The Line-In so far was never returned, even
;; with the USB Interface connected.
(def recording-lines
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

;; Create a RAW file format that can be played like this:
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
