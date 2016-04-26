(ns vr-logorrhoe.core
  (:gen-class)
  (:use seesaw.core)
  (:require [vr-logorrhoe.gui :as gui]
            [vr-logorrhoe.sound-input :as sound-input]))

(defn -main [& args]
  (gui/start))

;; (-main)
