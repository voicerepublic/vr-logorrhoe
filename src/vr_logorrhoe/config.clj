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
    (utils/conj-path app-directory "bin/lame")
    "Linux"
    "lame"))

(def assets-path (utils/conj-path app-directory "assets"))

(def default-config {:config-version 1
                     :recording-device ""
                     :log-file (utils/conj-path app-directory "messages.log")
                     :log-level :info
                     :backup-folder app-directory
                     :sample-size "16"
                     :audio-channels "2"
                     :sample-freq "44100"
                     :identifier (utils/generate-identifier)})

(defn- write-default-config-file []
  "Check whether there's a *re-stream* config folder and config
  file. If not, set those up."
  (if (not (utils/folder-exists? app-directory))
    (utils/create-folder app-directory))
  (spit config-file-path default-config))

;; `settings` are permanent settings like a backup folder that will
;; also be persisted in the config file.
(def ^{:private true} settings (atom (if (utils/path-exists? config-file-path)
                      (read-string (slurp config-file-path))
                      (do (write-default-config-file)
                          default-config))))

(defn setting
  "When given a key, return the value of the setting. When given a key
  and value, update the setting"
  ([key val]
   (swap! settings assoc key val))
  ([key]
   (key @settings)))

;; `app-state` are ephemeral settings that should not be persisted
(def ^{:private true} app-state (atom {}))

(defn state
  "When given a key, return the value of the state. When given a key
  and value, update the state"
  ([key val]
   (swap! app-state assoc key val))
  ([key]
   (key @app-state)))

(defn merge-state!
  "Merges the given map into the existing app-state, overwriting
  any existing values."
  [new-state]
  (swap! app-state merge new-state))

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
