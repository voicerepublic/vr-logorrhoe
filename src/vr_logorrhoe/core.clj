(ns vr-logorrhoe.core
  (:gen-class)
  (:require [vr-logorrhoe
             [gui :as gui]
             [config :as config]
             [utils :as utils]]))

(defn- setup-encoder-binary []
  "Install the encoder binary if not yet available"
  (if-not (utils/path-exists? (config/encoder-path))
    ;; potentially relevant for the future: os.version / os.arch
    (case (System/getProperty "os.name")
      "Mac OS X"
      (do
        (utils/copy-file-from-resource "bin/lame" (config/encoder-path))
        (clojure.java.shell/sh "chmod" "+x" (config/encoder-path))))))

(defn- bootstrap []
  "Initial setup: encoder, assets"
  (setup-encoder-binary))

(defn -main [& args]
  (gui/start))
