(ns vr-logorrhoe.config
  (:require [vr-logorrhoe.utils :as utils]))

(def user-home (System/getProperty "user.home"))

(def app-name "vr-logorrhoe")

(def config-directory (utils/conj-path user-home
                                         (str "." app-name)))

(def config-file (utils/conj-path config-directory
                                  "settings.edn"))

(defn- write-default-config-file []
  "Check whether there's a *re-stream* config folder and config
  file. If not, set those up."
  (if (not (utils/folder-exists? config-directory))
    (utils/create-folder config-directory))
  (spit config-file { :a 1 }))

(def settings (atom (if (utils/path-exists? config-file)
                      (read-string (slurp config-file))
                      (write-default-config-file))))

;; Whenever the settings of the application changes, save this new
;; configuration to the disk
(add-watch settings :watcher
  (fn [key atom old-state new-state]
    (spit config-file @atom)))
