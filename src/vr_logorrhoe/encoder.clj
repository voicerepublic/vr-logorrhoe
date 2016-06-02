(ns vr-logorrhoe.encoder
  (:require [clojure.java.shell2 :refer [sh]]))

(defn encode [input callback]
  "Encodes an input-stream using `lame` and pipes the result into the
  `callback` function"
  ;; TODO: Read frequency and bitwidth from @config
  (sh "lame" "-r" "--cbr" "-b" "256" "-s" "44.1" "--bitwidth" "16" "--signed" "--little-endian" "-m" "m" "-" "-"
      :in input
      :err #(do (prn "lame has written to stderr!")
                ;; TODO: `print-input-stream` doesn't print anything
                ;; useful for this type of InputStream as of now.
                (vr-logorrhoe.utils/print-input-stream %))
      :out #(callback %)))
