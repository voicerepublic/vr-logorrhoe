;; Documentation here:
;; https://github.com/OlegKunitsyn/libshout-java

(ns vr-logorrhoe.shout)

(def shout-config (atom {:host "52.58.144.148"
                         :port 80
                         :password "jrzdcupa"
                         :mount "/ba7174b2-c524-4b37-8611-3c21ac1f64e3"
                         :libshout nil}))

(defn- libshout []
  "Shorthand to access libshout instance"
  (:libshout @shout-config))

(defn- instantiate-libshout []
  (swap! shout-config assoc :libshout (com.gmail.kunicins.olegs.libshout.Libshout.)))

(defn connect []
  (instantiate-libshout)
  (prn "Connecting to Icecast Server")
  (.setHost (libshout) (:host @shout-config))
  (.setPort (libshout) (:port @shout-config))
  (.setProtocol (libshout) com.gmail.kunicins.olegs.libshout.Libshout/PROTOCOL_HTTP)
  (.setPassword (libshout) (:password @shout-config))
  (.setMount (libshout) (:mount @shout-config))
  (.setFormat (libshout) com.gmail.kunicins.olegs.libshout.Libshout/FORMAT_MP3)
  (.open (libshout) )
  (prn "Connected to Icecast Server"))

(defn disconnect []
  (prn "Disconnecting from Icecast Server")
  (.close (libshout))
  (prn "Disconnected from Icecast Server"))

;; This number has been taken from the ezstream.c method sendStream()
;; It has been observed that when trying to stream a lesser amount of
;; bytes, that (libshout) will core-dump.
(def stream-sample-size 4150)

(defn stream [input-stream]
  "Takes an input-stream and starts streaming to Icecast"
  (prn "vr-logorrhoe.shout: Starting to stream")
  (future
    (let [buffer (make-array (. Byte TYPE) stream-sample-size)]
      (loop []
        (let [size (.read input-stream buffer)]
          (prn "vr-logorrhoe.shout: Read 'size' bytes: " size)
          ;; KLUDGE: If the sample-size does not match 4150, libshout might core-dump!
          (if (> size 0 )
            (do
              (.send (libshout) buffer size)
              (recur)))))
      (prn "Closing input stream")
      (.close input-stream))))

(comment
  (connect)
  (stream (new java.io.FileInputStream
               "/home/munen/src/voicerepublic_icecast_tests/clients/test.mp3"))


  (disconnect)
  )
