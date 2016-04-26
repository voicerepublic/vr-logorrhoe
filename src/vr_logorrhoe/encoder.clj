(ns vr-logorrhoe.encoder
  (:use [clojure.java.shell :only [sh]]))

(defn encode [input]
  "Encodes an input [Byte-Array] using `lame`. Returns a map with
   `:out` holding the encoded data as Byte-Array."
  (sh "lame" "-r" "--cbr" "-b" "256" "-s" "44.1" "--bitwidth" "16" "--signed" "--little-endian" "-m" "m" "-" "-"
      :in input
      :out-enc :bytes))
