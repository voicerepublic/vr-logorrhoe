(ns vr-logorrhoe.core
  (:require [vr-logorrhoe.gui :as gui])
  (:gen-class))

(defn -main [& args]
  (gui/start))
