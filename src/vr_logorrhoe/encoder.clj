(ns vr-logorrhoe.encoder
  (:use [clojure.java.shell :only [sh]]))

(defn encode [input-file]
  (sh "lame" "-r" "-s" "44.1" "--bitwidth" "16" "--signed" "--little-endian" "-m" "m" "-" "-"
      :in input-file
      :out-enc :bytes))
