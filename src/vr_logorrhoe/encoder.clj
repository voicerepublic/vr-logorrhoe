(ns vr-logorrhoe.encoder
  (:use [clojure.java.shell :only [sh]]))

(defn encode [input]
  "Encodes an input [Byte-Array] using `lame`. Returns a map with
   `:out` holding the encoded data as Byte-Array."
  (sh "lame" "-r" "--cbr" "-b" "48" "-s" "44.1" "--bitwidth" "16" "--signed" "--little-endian" "-m" "m" "-" "-"
      :in input
      :out-enc :bytes))

;; This is a test scenario that shows that longer output can be
;; catpured using clojure.java.shell/sh:

;; (def test-file (.getChannel (java.io.FileOutputStream. "test.wav")))

;; (defn cat-file []
;;   (sh "cat" "clojure.wav" :out-enc :bytes))

;; (let [{out :out err :err exit :exit}  (cat-file)]
;;   (prn "Exit code: " exit)
;;   (prn "StdErr: " err)
;;   (.write test-file (. ByteBuffer (wrap out)))
;;   (println "Received bytes: " (count out)))

;; (.close output)
