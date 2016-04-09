(ns vr-logorrhoe.gui
  (:gen-class)
  (:use seesaw.core
        seesaw.font
        seesaw.chooser)
  (:require [vr-logorrhoe.sound-input :as sound-input]))

(def app-state (atom {:recording false}))

(def icons {:rec  "resources/rec.png"
            :stop "resources/stop.png"
            :logo "resources/logo.png"})

;; Helpers
(native!)

(def help-action (action
                  :handler (fn [e] (clojure.java.browse/browse-url "http://voicerepublic.com"))
                  :name "Documentation"
                  :tip  "Open Documentation"))

(def exit-action (action
                  :handler (fn [e] (.dispose (to-frame e)))
                  :name "Exit"
                  :tip  "Close this window"))
(def backup-folder-action (action
                           :handler (fn [e]
                                      (choose-file :remember-directory? true
                                                   :selection-mode :dirs-only
                                                   :success-fn (fn [fc file]
                                                                 (alert (str "Selected: " (.getAbsolutePath file))))))
                           :name "Choose Backup Folder"
                           :tip  "Choose Backup Folder"))


(def f (frame :title "VR - *re:stream*"
              :menubar
              (menubar :items
                       [(menu :text "File" :items [backup-folder-action exit-action])
                        (menu :text "Help" :items [help-action])])))

(defn display [content]
  (config! f :content content)
  content)


(defn start []
  (invoke-later
   (-> f pack! show! ))
  (config f :title)

  (let [logo (label
              :icon (java.io.File. (:logo icons)))
        title (label
               :text "VR - *re:stream* - Venue Title"
               :font (font :name :monospaced
                           :style #{:bold}
                           :size 34))
        btn (label
             :icon (java.io.File. (:rec icons)))
        audio-inputs (listbox :model ["Microphone" "Line-In"])
        split (left-right-split (scrollable audio-inputs) btn :divider-location 1/3)]

    (display (border-panel
              :north (horizontal-panel :items [logo (label :text "       ") title])
              :south (label :text "Brought to you via voicerepublic.com")
              :center split
              :vgap 5 :hgap 5 :border 5))

    ;; Register actions
    (listen audio-inputs :selection
            (fn[e]
              (when-let [s (selection e)]
                (alert (selection e)))))

    (listen btn :mouse-clicked (fn[e]
                                 (swap! app-state
                                        assoc :recording
                                        (not (:recording @app-state)))
                                 (config! btn
                                          :icon (java.io.File. (if (:recording @app-state)
                                                                 (:stop icons)
                                                                 (:rec icons))))))))


;; (start)
