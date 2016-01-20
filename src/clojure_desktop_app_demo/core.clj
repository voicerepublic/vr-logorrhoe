(ns clojure-desktop-app-demo.core
  (:gen-class)
  (:use seesaw.core)
  (:require [net.n01se.clojure-jna :as jna]))


(defn crazy-batshit-from-c [num]
  (jna/invoke Integer c/printf "The number given: %d\n" num))


(defn -main [& args]
  (native!)
  (invoke-later
    (-> (frame :title "Hello VR",
           :content "Hello, VR - this is from Clojure!",
           :on-close :exit)
     pack!
     show!)))

;; (-main)

;; (crazy-batshit-from-c 123)
