(ns vr-logorrhoe.core
  (:gen-class)
  (:require [vr-logorrhoe
             [vrapi :as vrapi]
             [logging :as logging]
             [checks :as checks]
             [gui :as gui]
             [config :as config]
             [utils :as utils]]
            [clojure.java.shell :refer [sh]]))


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
          (sh "chmod" "+x" (config/encoder-path)))

        ;; For Linux, there's nothing to install since the
        ;; dependencies are defined in the package. However, this is a
        ;; good place to make sure those dependencies got installed.
        "Linux"
        (if (nil? (re-seq #"lame" (.toLowerCase (:err (sh "lame")))))
          (utils/die "Error: Could no find `lame` in PATH."))

        ;; default
        true))))

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
  (logging/setup!)
  (checks/check-connectivity)
  (checks/check-version)
  (vrapi/knock)
  (vrapi/register)
  (bootstrap)
  (vrapi/start-polling)
  (gui/start))
