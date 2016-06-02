(ns vr-logorrhoe.config
  (:require [vr-logorrhoe.utils :as utils]))

;; TODO: This yields "/tmp" on the first run, when called a second
;; time it yields the correct value. Strange stuff.
(def user-home (System/getProperty "user.home"))

(def app-name "vr-logorrhoe")

(def config-directory (utils/conj-path user-home
                                       (str "." app-name)))

(def config-file (utils/conj-path config-directory
                                  "settings.edn"))

(def default-config {:recording-device ""
                     :sample-freq "44100"
                     :sample-size "16"
                     :audio-channels "2"})

(defn- write-default-config-file []
  "Check whether there's a *re-stream* config folder and config
  file. If not, set those up."
  (if (not (utils/folder-exists? config-directory))
    (utils/create-folder config-directory))
  (spit config-file default-config))

;; `settings` are potential permanent settings like a backup folder
(def settings (atom (if (utils/path-exists? config-file)
                      (read-string (slurp config-file))
                      (do
                        (write-default-config-file)
                        default-config))))

(defn update-setting [key val]
  (swap! settings assoc key val))

;; (swap! settings conj {:recording-device "Intel [plughw:0,1]"})

;; `config` are ephemeral settings that should not be persisted
(def config (atom { :foo 1
                   }))

;; Whenever the settings of the application changes, save this new
;; configuration to the disk
(add-watch settings :watcher
           (fn [key atom old-state new-state]
             (spit config-file @atom)))
