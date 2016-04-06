(ns vr-logorrhoe.core
  (:gen-class)
  (:use seesaw.core)
  (:require
   ;; [net.n01se.clojure-jna :as jna]

            [clj-http.client :as client]
            [vr-logorrhoe.gui :as gui]
            [vr-logorrhoe.audio :refer [record]]))



(defn -main [& args]
  (gui/start))

(-main)


;; (defn crazy-batshit-from-c [num]
;;   (jna/invoke Integer c/printf "The number given: %d\n" num))
;; (crazy-batshit-from-c 112423)
