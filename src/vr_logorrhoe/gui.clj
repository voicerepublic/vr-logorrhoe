(ns vr-logorrhoe.gui
  (:require [clojure.java
             [browse :refer [browse-url]]
             [io :as io]]
            [seesaw
             [chooser :refer :all]
             [core :refer :all]
             [font :refer :all]]
            [vr-logorrhoe
             [checks :as checks]
             [config :as config]
             [shout :as shout]
             [recorder :as recorder]
             [utils :as utils :refer [log]]]))

;; Declare initial state
(config/state :record-button false)

(def icons {:logo (io/file (utils/conj-path config/assets-path "logo.png"))})

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
(def check-update-action (action
                          :name "Check for Updates"
                          :handler (fn [e]
                                     (checks/check-version)
                                     (alert "Your software is up to date."))))

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
                                                                 (config/setting :backup-folder (.getAbsolutePath file)))))
                           :name "Choose Backup Folder"
                           :tip  "Choose Backup Folder"))

;; Setup the one frame to hold the whole GUI
(def f (frame :title "VR - *re:stream*"
              :menubar
              (menubar :items
                       [(menu :text "File" :items [backup-folder-action
                                                   exit-action])
                        (menu :text "Help" :items [check-update-action
                                                   help-action])])))

(defn start []
  "Starts the GUI"
  (invoke-later
   (-> f pack! show! ))
  (config f :title)

  (let [logo (label
              :icon (:logo icons))
        title (label
               :text "VR - *re:stream*"
               :font (font :name :monospaced
                           :style #{:bold}
                           :size 34))
        record-button (button :text "Record")
        audio-inputs (listbox :model (recorder/get-mixer-names))
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
                      :hgap 20
                      :items [audio-sample-freq
                              audio-sample-size
                              audio-channels])
        server-field (text (config/setting :host))
        password-field (text (config/setting :password))
        mountpoint-field (text (config/setting :mountpoint))
        left-main (top-bottom-split
                   (top-bottom-split audio-format
                                     (horizontal-panel :items [server-field
                                                               password-field
                                                               mountpoint-field]))
                   (scrollable audio-inputs))

        main (left-right-split left-main record-button :divider-location (/ 1 1.5))
        freq-col ["22050" "44100" "48000"]
        channels-col ["1" "2"]
        sample-size-col ["16" "24" "32"]]

    (selection! audio-inputs (config/setting :recording-device))

    (populate-combo-box audio-channels-combo-box
                        channels-col
                        (utils/index-of (config/setting :audio-channels) channels-col))

    (populate-combo-box audio-sample-freq-combo-box
                        freq-col
                        (utils/index-of (config/setting :sample-freq) freq-col))

    (populate-combo-box audio-sample-size-combo-box
                        sample-size-col
                        (utils/index-of (config/setting :sample-size) sample-size-col))

    ;; Set up the GUI layout
    (config! f :content (border-panel
                         :north (horizontal-panel :items [logo (label :text "       ") title])
                         :south (label :text "Brought to you via voicerepublic.com")
                         :center main
                         :vgap 5 :hgap 5 :border 5))

    ;; Register actions
    (listen server-field
            :focus-lost
            (fn [e]
              (when-let [t (text server-field)]
                (shout/set-host t))))

    (listen password-field
            :focus-lost
            (fn [e]
              (when-let [t (text password-field)]
                (shout/set-password t))))

    (listen mountpoint-field
            :focus-lost
            (fn [e]
              (when-let [t (text mountpoint-field)]
                (shout/set-mountpoint t))))

    (listen audio-inputs :selection (fn[e]
                                      (when-let [s (selection e)]
                                        (config/setting :recording-device s))))

    (listen audio-channels-combo-box :selection (fn[e]
                                        (when-let [s (selection e)]
                                          (config/setting :audio-channels s))))

    (listen audio-sample-freq-combo-box :selection (fn[e]
                                                     (when-let [s (selection e)]
                                                       (config/setting :sample-freq s))))

    (listen audio-sample-size-combo-box :selection (fn[e]
                                                     (when-let [s (selection e)]
                                                       (config/setting :sample-size s))))
    (listen record-button
            :mouse-clicked
            (fn[e]

              (config/state :record-button not)

              (config! record-button
                       :text (if (config/state :record-button)
                               "Stop"
                               "Record"))

              (future
                (if (config/state :record-button)
                  (recorder/start-recording)
                  (recorder/stop-recording))))))

  ;; Set size after everything else is in the frame, otherwise the
  ;; size in Windows will be set to 0x0 anyway.
  (config! f :size [900 :by 500]))


;; (vr-logorrhoe.core/-main)
;; (start)
