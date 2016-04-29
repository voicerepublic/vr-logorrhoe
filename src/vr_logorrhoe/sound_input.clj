;; Well written tutorial on the Java Sampled Package:
;; http://docs.oracle.com/javase/tutorial/sound/sampled-overview.html

(ns vr-logorrhoe.sound-input
  (:require [vr-logorrhoe
             [encoder :refer [encode]]
             [shout :as shout]])
  (:import [java.io ByteArrayInputStream ByteArrayOutputStream]
           java.lang.Thread
           java.nio.ByteBuffer
           [javax.sound.sampled AudioFormat AudioSystem LineListener]))

;; Note: It would be possible to check for LINE_IN and MICROPHONE
;; directly using
;; (AudioSystem/isLineSupported Port$Info/LINE_IN)
;; and
;; (AudioSystem/getLine Port$Info/LINE_IN)
;; However, this does not always return a line with said capability
;; (seen in Debian 8). Therefore, we're using the method to request a
;; line via the Mixer.
(defn get-mixer-info []
  "Retrieve supported mixers from OS"
  (seq (. AudioSystem (getMixerInfo))))


(defn get-mixer-infos []
  "Returns mixer-info, name, description of each mixer"
  (map #(let [m %] {:mixer-info m
                    :name (. m (getName))
                    :description (. m (getDescription))})
       (get-mixer-info)))

;; This method is called by the GUI to query for available mixers
(defn get-mixer-names []
  "Returns all available Mixers by name [String]"
  (map :name (get-mixer-infos)))

(defn get-recorder-mixer-info [recorder-name]
  "Returns Mixer Info for a specific recorder, queried by name [String]"
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
                        (getLine line-info))
                     (catch Exception e
                       (println "Exception with getting the line for mixer: " e)
                       false)))

;; Add a line listener for events on the line. This is an optional step
;; and is currently only used for logging purposes.
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

;; The size of the buffer is deliberately 1/5th of the lines
;; buffer. Otherwise there's a racing condition between the mixer
;; writing to and this code reading from the buffer.
;; This needs to be called before the line is open.
(def mic-buffer-size (int (/ (.getBufferSize recorder-line) 5)))

(defn- write-buffer-to-file [buffer file]
  "Takes a ByteBuffer and writes it into a FileChannel"
  (future
    (.write file buffer)))

(defn record []
  ;; Open the Port
  (. recorder-line (open audio-format mic-buffer-size))

  ;; Start Audio Capture
  (. recorder-line (start))

  (let [raw-file (.getChannel (java.io.FileOutputStream. "clojure.wav"))
        mp3-file (.getChannel (java.io.FileOutputStream. "clojure.mp3"))
        output-stream (new ByteArrayOutputStream)]

    (dotimes [i 10]
      (let [buffer    (make-array (. Byte TYPE) mic-buffer-size)
            bcount    (. recorder-line (read buffer 0 mic-buffer-size))
            ;; Current sample
            bbyte-tmp (. ByteBuffer (wrap buffer))]

        ;; Successively write sample after sample in raw format
        (write-buffer-to-file bbyte-tmp raw-file)

        (prn "Start encoding!")
        (let [{out :out err :err exit :exit}  (encode buffer)
              bbyte-mp3 (. ByteBuffer (wrap out))]
          (prn "StdErr: " err)
          (println "Received bytes: " (count out))

          (write-buffer-to-file bbyte-mp3 mp3-file)

          (. output-stream (write out 0 (count out)))
          (prn "Current size of output-stream: " (.size output-stream))

          (if (= i 9)
            (let [bytea   (.toByteArray output-stream)
                  input-s (new ByteArrayInputStream bytea)]
              (shout/stream input-s)))))

      ;; TODO: Call the `drain` method to drain the recorder-line when
      ;; the recording stops. Otherwise the recorded data might seem
      ;; to end pre-maturely.
      (. Thread (sleep 20)))

    ;; stop the input
    (. recorder-line (stop))

    ;; close recorder
    (. recorder-line (close))

    (.close raw-file)
    (.close mp3-file)))

(comment
  (shout/connect)

  (try
    (record)
    (catch Exception e
      (println "Caught: " e)
      (. recorder-line (stop))
      (. recorder-line (close))
      ))

  (shout/disconnect)
  )
