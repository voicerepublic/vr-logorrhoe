(ns vr-logorrhoe.gui
  (:require [clojure.java.browse :refer [browse-url]]
            [seesaw
             [chooser :refer :all]
             [core :refer :all]
             [font :refer :all]]
            [vr-logorrhoe
             [config :as config]
             [sound-input :as sound-input]
             [utils :as utils]]))

;; Declare some state
(def app-state (atom {:recording false}))

(def icons {:rec  "resources/rec.png"
            :stop "resources/stop.png"
            :logo "resources/logo.png"})

;; Before any UI is created, tell seesaw to make things look as native
;; as possible
(native!)

;; Helper functions
(defn- create-combo-box []
  "Creates a new JComboBox seesaw-widget"
  (seesaw.core/make-widget (new javax.swing.JComboBox)))

(defn- populate-combo-box [combo-box col selected-element-pos]
  "Populates a `combo-box` with a collection `col` and pre-selects the
  element at position `selected-element-pos`"
  (doall
   (map #(.addItem combo-box %) col))
  (.setSelectedIndex combo-box
                     selected-element-pos))

;; Menu Action Helpers
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

;; Setup the one frame to hold the whole GUI
(def f (frame :title "VR - *re:stream*"
              :menubar
              (menubar :items
                       [(menu :text "File" :items [backup-folder-action exit-action])
                        (menu :text "Help" :items [help-action])])))

(defn start []
  "Starts the GUI"
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
        record-button (label
             :icon (java.io.File. (:rec icons)))
        audio-inputs (listbox :model (sound-input/get-mixer-names))
        ;; TODO: Add the other audio-format configuration parameters
        ;; -> int channels, boolean signed, boolean bigEndian
        audio-sample-freq-combo-box (create-combo-box)
        audio-sample-freq (left-right-split (label :text "Frequency")
                                            audio-sample-freq-combo-box)
        audio-sample-size-combo-box (create-combo-box)
        audio-sample-size (left-right-split (label :text "Sample Size")
                                            audio-sample-size-combo-box)
        audio-channels-combo-box (create-combo-box)
        audio-channels (left-right-split (label :text "Channels")
                                            audio-channels-combo-box)
        audio-format (flow-panel
                      ;; :align :left
                      :hgap 20
                      :items [audio-sample-freq
                              audio-sample-size
                              audio-channels])
        left-main (top-bottom-split audio-format
                                    (scrollable audio-inputs))
        main (left-right-split left-main record-button :divider-location 1/3)
        freq-col ["22050" "44100" "48000"]
        channels-col ["1" "2"]
        sample-size-col ["16" "24" "32"]]

    (selection! audio-inputs (:recording-device @config/settings))

    (populate-combo-box audio-channels-combo-box
                        channels-col
                        (utils/index-of (:audio-channels @config/settings) channels-col))

    (populate-combo-box audio-sample-freq-combo-box
                        freq-col
                        (utils/index-of (:sample-freq @config/settings) freq-col))

    (populate-combo-box audio-sample-size-combo-box
                        sample-size-col
                        (utils/index-of (:sample-size @config/settings) sample-size-col))

    ;; Set up the GUI layout
    (config! f :content (border-panel
                         :north (horizontal-panel :items [logo (label :text "       ") title])
                         :south (label :text "Brought to you via voicerepublic.com")
                         :center main
                         :vgap 5 :hgap 5 :border 5))

    ;; Register actions
    (listen audio-inputs :selection (fn[e]
                                      (when-let [s (selection e)]
                                        (config/update-setting :recording-device s))))

    (listen audio-channels-combo-box :selection (fn[e]
                                        (when-let [s (selection e)]
                                          (config/update-setting :audio-channels s))))

    (listen audio-sample-freq-combo-box :selection (fn[e]
                                                     (when-let [s (selection e)]
                                                       (config/update-setting :sample-freq s))))

    (listen audio-sample-size-combo-box :selection (fn[e]
                                                     (when-let [s (selection e)]
                                                       (config/update-setting :sample-size s))))

    (listen record-button :mouse-clicked (fn[e]
                                 (config! record-button
                                          :icon (java.io.File. (if (:recording @app-state)
                                                                 (:stop icons)
                                                                 (:rec icons))))
                                 (swap! app-state
                                        assoc :recording
                                        (not (:recording @app-state)))))))


;; (start)
