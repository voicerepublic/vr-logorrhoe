;; Documentation here:
;; https://github.com/OlegKunitsyn/libshout-java

(ns vr-logorrhoe.shout)


(def libshout (com.gmail.kunicins.olegs.libshout.Libshout.))
(.getVersion libshout)

(def buffer (make-array (. Byte TYPE) 4150) )

(def input-stream (new java.io.BufferedInputStream (new java.io.FileInputStream (new java.io.File "/home/munen/src/voicerepublic_icecast_tests/clients/test.mp3"))))

(.setHost libshout "52.58.65.224")
(.setPort libshout 80)
(.setProtocol libshout com.gmail.kunicins.olegs.libshout.Libshout/PROTOCOL_HTTP)
(.setPassword libshout "thisisagoodpassword")
(.setMount libshout "/4609")
(.setFormat libshout com.gmail.kunicins.olegs.libshout.Libshout/FORMAT_MP3)
(.open libshout )

(loop []
  (let [size (.read input-stream buffer)]
    (when (> size )
      (.send libshout buffer size)
      (recur))))

(.close libshout)
(.close input-stream)
