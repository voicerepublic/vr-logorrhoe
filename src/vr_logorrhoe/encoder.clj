(ns vr-logorrhoe.encoder
  (:require [clojure.java.shell2 :refer [sh]]))

(defn encode [input callback]
  "Encodes an input-stream using `lame` and pipes the result into the
  `callback` function"
  (sh "lame" "-r" "--cbr" "-b" "256" "-s" "44.1" "--bitwidth" "16" "--signed" "--little-endian" "-m" "m" "-" "-"
      :in input
      ;; Pass stderr to JVM
      :err :pass
      :out #(callback %)))
