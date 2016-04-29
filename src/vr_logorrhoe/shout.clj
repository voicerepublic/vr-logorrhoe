;; Documentation here:
;; https://github.com/OlegKunitsyn/libshout-java

(ns vr-logorrhoe.shout)

(def shout-config (atom {:host "52.58.144.148"
                         :port 80
                         :password "jrzdcupa"
                         :mount "/ba7174b2-c524-4b37-8611-3c21ac1f64e3"}))

(def libshout (com.gmail.kunicins.olegs.libshout.Libshout.))
;; (.getVersion libshout)

;; TODO: Catch java.io.IOException
(defn connect []
  (.setHost libshout (:host @shout-config))
  (.setPort libshout (:port @shout-config))
  (.setProtocol libshout com.gmail.kunicins.olegs.libshout.Libshout/PROTOCOL_HTTP)
  (.setPassword libshout (:password @shout-config))
  (.setMount libshout (:mount @shout-config))
  (.setFormat libshout com.gmail.kunicins.olegs.libshout.Libshout/FORMAT_MP3)
  (.open libshout ))

(defn stream [input-stream]
  "Takes an input-stream and starts streaming to Icecast"
  (prn "vr-logorrhoe.shout: Starting to stream")
  (prn "Type of input-stream: " (type input-stream))
  (future
    (let [buffer (make-array (. Byte TYPE) 4150)]
      (loop []
        (let [size (.read input-stream buffer)]
          (prn "Actually streaming!")
          (prn "Streaming size: " size)
          (when (> size )
            (.send libshout buffer size)
            (recur)))))
    (.close input-stream)))

(defn disconnect []
  (.close libshout))

(comment
  (connect)
  (stream (new java.io.FileInputStream
               "/home/munen/src/voicerepublic_icecast_tests/clients/test.mp3"))


  (disconnect)
  )
