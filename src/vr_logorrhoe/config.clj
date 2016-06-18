(ns vr-logorrhoe.config
  (:require [vr-logorrhoe
             [utils :as utils :refer [log]]]
            [clojure.java.io :as io]))

;; TODO: This yields "/tmp" on the first run, when called a second
;; time it yields the correct value. Strange stuff.  Just doing it
;; twice here, even with some seconds of sleep in between is not
;; cutting it, tough!
(def user-home-path (System/getProperty "user.home"))

(def app-name "vr-logorrhoe")

(def app-directory (utils/conj-path user-home-path
                                    (str "." app-name)))

(def config-file-path (utils/conj-path app-directory "settings.edn"))

(defn encoder-path []
  (case (System/getProperty "os.name")
    "Mac OS X"
    (utils/conj-path app-directory "lame")
    "Linux"
    "lame"))

(def default-config {:recording-device ""
                     :sample-freq "44100"
                     :log-file (utils/conj-path app-directory "messages.log")
                     :backup-folder app-directory
                     :sample-size "16"
                     :audio-channels "2"
                     :host "127.0.0.1"
                     :password "thisisnotagoodpassword"
                     :mountpoint "i_am_a_mountpoint"})

(defn- write-default-config-file []
  "Check whether there's a *re-stream* config folder and config
  file. If not, set those up."
  (if (not (utils/folder-exists? app-directory))
    (utils/create-folder app-directory))
  (spit config-file-path default-config))

(defn- setup-encoder-binary []
  "Install the encoder binary if not yet available"
  (if-not (utils/path-exists? (encoder-path))
    ;; potentially relevant for the future: os.version / os.arch
    (case (System/getProperty "os.name")
      "Mac OS X"
      (do
        (utils/copy-file-from-resource "bin/lame" (encoder-path))
        (clojure.java.shell/sh "chmod" "+x" (encoder-path))))))

(defn- setup-application
  "Setup initial requirements: config folder/file, encoder"
  []
  ;; TODO: These should be two separate things. Maybe a `setup` ns is
  ;; a good idea.
  (write-default-config-file)
  (setup-encoder-binary))

;; `settings` are permanent settings like a backup folder that will
;; also be persisted in the config file.
(def settings (atom (if (utils/path-exists? config-file-path)
                      (read-string (slurp config-file-path))
                      (do (setup-application)
                          default-config))))

(defn update-setting [key val]
  (swap! settings assoc key val))

;; `app-state` are ephemeral settings that should not be persisted
(def app-state (atom {}))

;; Initially set the logger path
(utils/set-logger-path (:log-file @settings))

;; Whenever the settings of the application changes, save this new
;; configuration to the disk
(add-watch settings :watcher
           (fn [key atom old-state new-state]
             (spit config-file-path @atom)))

;; Whenever the settings of the application changes, save this new
;; configuration to the disk
(add-watch settings :settings-watcher
           (fn [key atom old-state new-state]
             (spit config-file-path @atom)))

;; Whenever the app-state changes, print the change
(add-watch app-state :app-state-watcher
           (fn [key atom old-state new-state]
             (log "app state changed to: " @atom)))
