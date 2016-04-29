(ns vr-logorrhoe.gui
  (:require [clojure.java.browse :refer [browse-url]]
            [seesaw
             [chooser :refer :all]
             [core :refer :all]
             [font :refer :all]]
            [vr-logorrhoe.sound-input :as sound-input]))

(def app-state (atom {:recording false}))

(def icons {:rec  "resources/rec.png"
            :stop "resources/stop.png"
            :logo "resources/logo.png"})

;; Helpers
(native!)

(def help-action (action
                  :handler (fn [e] (browse-url "http://voicerepublic.com"))
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

;; TODO: Generalise the creation of the audio settings combo-boxes
;; TODO: Add the other audio-format configuration parameters
;; -> float sampleRate, int sampleSizeInBits, int channels, boolean signed, boolean bigEndian
(def audio-sample-freq-combo-box (seesaw.core/make-widget (new javax.swing.JComboBox)))
(defn- populate-audio-freq-combo-box []
  (doall
   (map #(.addItem audio-sample-freq-combo-box %) ["22050" "44100" "48000"]))
  (.setSelectedIndex audio-sample-freq-combo-box 1))

(def audio-sample-size-combo-box (seesaw.core/make-widget (new javax.swing.JComboBox)))
(defn- populate-audio-sample-size-combo-box []
  (doall
   (map #(.addItem audio-sample-size-combo-box %) ["16" "24" "32"])))

(defn start []
  (invoke-later
   (-> f pack! show! ))
  (config f :title)

  (populate-audio-freq-combo-box)
  (populate-audio-sample-size-combo-box)

  (let [logo (label
              :icon (java.io.File. (:logo icons)))
        title (label
               :text "VR - *re:stream* - Venue Title"
               :font (font :name :monospaced
                           :style #{:bold}
                           :size 34))
        btn (label
             :icon (java.io.File. (:rec icons)))
        audio-inputs (listbox :model (sound-input/get-mixer-names))
        audio-sample-freq (left-right-split (label :text "Frequency")
                                            audio-sample-freq-combo-box)
        audio-sample-size (left-right-split (label :text "Sample Size")
                                            audio-sample-size-combo-box)
        audio-format (flow-panel
                      ;; :align :left
                      :hgap 20
                      :items [audio-sample-freq
                              audio-sample-size])
        left-main (top-bottom-split audio-format
                                    (scrollable audio-inputs))
        main (left-right-split left-main btn :divider-location 1/3)]

    (display (border-panel
              :north (horizontal-panel :items [logo (label :text "       ") title])
              :south (label :text "Brought to you via voicerepublic.com")
              :center main
              :vgap 5 :hgap 5 :border 5))

    ;; Register actions
    (listen audio-inputs :selection (fn[e]
                                      (when-let [s (selection e)]
                                        (alert (selection e)))))

    (listen audio-sample-freq-combo-box :selection (fn[e]
                                                     (when-let [s (selection e)]
                                                       (alert s))))

    (listen audio-sample-size-combo-box :selection (fn[e]
                                                     (when-let [s (selection e)]
                                                       (alert s))))

    (listen btn :mouse-clicked (fn[e]
                                 (config! btn
                                          :icon (java.io.File. (if (:recording @app-state)
                                                                 (:stop icons)
                                                                 (:rec icons))))
                                 (swap! app-state
                                        assoc :recording
                                        (not (:recording @app-state)))))))


;; (start)
