(ns vr-logorrhoe.encoder
  (:require
   [vr-logorrhoe
    [config :as config]
    [utils :refer [log]]]
    [clojure.java.shell2 :refer [sh]]))

(defn- lame-freq []
  "Map between Hz and lame KHz notation"
  (get {"44100" "44.1"
        "22050" "22.05"
        "48000" "48"}
       (:sample-freq @config/settings)))

(defn- lame-mode []
  "Lame mode is either 'mono' or 'joint stereo'. See `man lame` for
  details"
  (case (:audio-channels @config/settings)
    "1" "m"
    "2" "j"))

(defn encode [input callback]
  "Encodes an input-stream using `lame` and pipes the result into the
  `callback` function"
  (sh "lame" "-r" "--cbr" "-b" "256" "-s" (lame-freq) "--bitwidth" (:sample-size @config/settings) "--signed" "--little-endian" "-m" (lame-mode) "-" "-"
      :in input
      :err #(do (log "lame has written to stderr!")
                ;; TODO: `print-input-stream` doesn't print anything
                ;; useful for this type of InputStream as of now.
                (vr-logorrhoe.utils/print-input-stream %))
      :out #(callback %)))
