(ns vr-logorrhoe.core
  (:gen-class)
  (:require [vr-logorrhoe
             [config :as config]
             [gui :as gui]]))

(defn -main [& args]
  (config/create-initial-configuration-setup)
  (gui/start))





;; (-main)
