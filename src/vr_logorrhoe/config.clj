(ns vr-logorrhoe.config
  (:require [vr-logorrhoe.utils :as utils]))

;; TODO: This yields "/tmp" on the first run, when called a second
;; time it yields the correct value. Strange stuff.  Just doing it
;; twice here, even with some seconds of sleep in between is not
;; cutting it, tough!
(def user-home (System/getProperty "user.home"))

(def app-name "vr-logorrhoe")

(def config-directory (utils/conj-path user-home
                                       (str "." app-name)))

(def config-file (utils/conj-path config-directory
                                  "settings.edn"))

(def default-config {:recording-device ""
                     :sample-freq "44100"
                     :sample-size "16"
                     :audio-channels "2"
                     :host "127.0.0.1"
                     :password "thisisnotagoodpassword"
                     :mountpoint "i_am_a_mountpoint"})

(defn- write-default-config-file []
  "Check whether there's a *re-stream* config folder and config
  file. If not, set those up."
  (if (not (utils/folder-exists? config-directory))
    (utils/create-folder config-directory))
  (spit config-file default-config))

;; `settings` are permanent settings like a backup folder that will
;; also be persisted in the config file.
(def settings (atom (if (utils/path-exists? config-file)
                      (read-string (slurp config-file))
                      (do
                        (write-default-config-file)
                        default-config))))

(defn update-setting [key val]
  (swap! settings assoc key val))

;; `app-state` are ephemeral settings that should not be persisted
(def app-state (atom {}))

;; Whenever the settings of the application changes, save this new
;; configuration to the disk
(add-watch settings :watcher
           (fn [key atom old-state new-state]
             (spit config-file @atom)))

;; Whenever the settings of the application changes, save this new
;; configuration to the disk
(add-watch settings :settings-watcher
           (fn [key atom old-state new-state]
             (spit config-file @atom)))

;; Whenever the app-state changes, print the change
(add-watch app-state :app-state-watcher
           (fn [key atom old-state new-state]
             (prn "app state changed to: " @atom)))
