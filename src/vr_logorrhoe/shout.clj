;; Documentation here:
;; https://github.com/OlegKunitsyn/libshout-java

(ns vr-logorrhoe.shout)

(def shout-config (atom {:host "52.58.65.224"
                         :port 80
                         :password "thisisagoodpassword"
                         :mount "/4609"}))

(def libshout (com.gmail.kunicins.olegs.libshout.Libshout.))
;; (.getVersion libshout)

(defn connect []
  (.setHost libshout (:host @shout-config))
  (.setPort libshout (:port @shout-config))
  (.setProtocol libshout com.gmail.kunicins.olegs.libshout.Libshout/PROTOCOL_HTTP)
  (.setPassword libshout (:password @shout-config))
  (.setMount libshout (:mount @shout-config))
  (.setFormat libshout com.gmail.kunicins.olegs.libshout.Libshout/FORMAT_MP3)
  (.open libshout ))

(defn stream [input-stream]
  (let [buffer (make-array (. Byte TYPE) 4150)]
    (loop []
      (let [size (.read input-stream buffer)]
        (when (> size )
          (.send libshout buffer size)
          (recur)))))
  (.close input-stream))

(defn disconnect []
  (.close libshout))

(comment
  (let [test-input-stream (new java.io.BufferedInputStream
                               (new java.io.FileInputStream
                                    (new java.io.File
                                         "/home/munen/src/voicerepublic_icecast_tests/clients/test.mp3")))]

    (connect)
    (stream test-input-stream)
    (disconnect)))
