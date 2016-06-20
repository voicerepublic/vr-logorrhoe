(ns vr-logorrhoe.core
  (:gen-class)
  (:require [vr-logorrhoe
             [checks :as checks]
             [gui :as gui]
             [config :as config]
             [utils :as utils]]))


(defn- setup-encoder-binary []
  "Install the encoder binary if not yet available"
  (if-not (utils/path-exists? (config/encoder-path))
    ;; potentially relevant for the future: os.version / os.arch
    (let [os-name (System/getProperty "os.name")]
      (case os-name
        "Mac OS X"
        (do
          (utils/create-folder (utils/conj-path config/app-directory "bin"))
          (utils/copy-file-from-resource "bin/lame" (config/encoder-path))
          (clojure.java.shell/sh "chmod" "+x" (config/encoder-path)))

        ;;"Linux"
        ;;(do
        ;;(utils/create-folder (utils/conj-path config/app-directory "bin"))
        ;;(utils/which "lame"))

        ;; default
        (utils/die "Error: No implementation for" os-name)))))

(defn- setup-assets []
  "Copies the image assets if not yet available"
  (if-not (utils/path-exists? config/assets-path)
    (do
      (utils/create-folder config/assets-path)
      (utils/copy-file-from-resource "assets/logo.png" (utils/conj-path config/assets-path "logo.png")))))

(defn- bootstrap []
  "Initial setup: encoder, assets"
  (setup-encoder-binary)
  (setup-assets))

(defn -main [& args]
  (checks/check-connectivity)
  (checks/check-version)
  (bootstrap)
  (gui/start))
