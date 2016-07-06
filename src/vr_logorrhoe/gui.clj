(ns vr-logorrhoe.gui
  (:require [clojure.java
             [browse :refer [browse-url]]
             [io :as io]]
            [seesaw
             [core :refer :all]
             [font :refer :all]
             [chooser :refer [choose-file]]
             [mig :refer [mig-panel]]]
            [vr-logorrhoe
             [config :as config]
             [shout :as shout]
             [recorder :as recorder]
             [utils :as utils :refer [log]]]))

;; --- dev notes
(comment
  (start)
  (and (load-file "src/vr_logorrhoe/gui.clj") (start))
  )

;; Table of Contents
;;
;; * state
;; * data
;; * helpers (generic parameterized component factories)
;; * components (specific component factories)
;; * handlers
;; * actions
;; * setup
;; * dev notes


;; --- state

(config/state :record-button false)


;; --- data

(def title "RE:STREAM")

(def icons {:logo (io/file (utils/conj-path config/assets-path "logo.png"))})

(def source-options (recorder/get-mixer-names))

(def frequency-options ["22050" "44100" "48000"])

(def channels-options ["1" "2"])

(def sample-size-options ["16" "24" "32"])


;; --- helpers

(defn- observed-combobox [options value handler]
  (let [combo (combobox :model options)]
    (selection! combo value)
    (listen combo :selection
            (fn [e] (handler (selection e))))
    combo))

(defn- observed-text [value handler]
  (let [field (text value)]
    (listen field :focus-lost
            (fn [e] (handler (text field))))
    field))

;; --- components

(defn- logo-comp []
  (label :icon (:logo icons)))

(defn- title-comp []
  (label :text title
         :font (font :name :monospaced
                     :style #{:bold}
                     :size 48)))

(defn- source-comp []
  (observed-combobox source-options
                     (config/setting :recording-device)
                     #(config/setting :recording-device %)))

(defn- frequency-comp []
  (observed-combobox frequency-options
                     (config/setting :sample-freq)
                     #(config/setting :sample-freq %)))

(defn- sample-size-comp []
  (observed-combobox sample-size-options
                     (config/setting :sample-size)
                     #(config/setting :sample-size %)))

(defn- channels-comp []
  (observed-combobox channels-options
                     (config/setting :audio-channels)
                     #(config/setting :audio-channels %)))


(defn- record-button-comp []
  (let [btn (button :text "Stream & Record")]
    (listen btn :mouse-clicked
            (fn [e]
              (config/state :record-button (not (config/state :record-button)))
              (config! btn :text (if (config/state :record-button) "Stop" "Stream & Record"))
              (future (if (config/state :record-button)
                        (recorder/start-recording)
                        (recorder/stop-recording)))))
    btn))


(defn- content-comp []
  (mig-panel :constraints ["" ; layout
                           "" ; cols
                           "[][]20[][]20[]"] ; rows
             :items [[(logo-comp) "span, wrap"] ; TODO fix logo
                     [(title-comp) "span, wrap, center"]

                     ["Source"]      [(source-comp)        "growx, span, wrap"]
                     ["Frequency"]   [(frequency-comp)     "growx"]
                     ["Sample Size"] [(sample-size-comp)   "growx"]
                     ["Channels"]    [(channels-comp)      "growx, wrap"]

                     ["" "span 3"]   [(record-button-comp) "growx, span 3"]]))


;; --- handlers

(defn backup-folder-handler [_]
  (choose-file :remember-directory? true
               :selection-mode :dirs-only
               :success-fn (fn [fc file]
                             (config/setting :backup-folder
                                             (.getAbsolutePath file)))))


;; --- actions

(def exit-action
  (action :name "Exit"
          :tip  "Close this window"
          :handler (fn [e] (.dispose (to-frame e)))))

(def backup-folder-action
  (action :name "Choose Backup Folder"
          :tip  "Choose Backup Folder"
          :handler backup-folder-handler))

(def help-action
  (action :name "Documentation"
          :tip  "Open Documentation"
          :handler (fn [_] (browse-url "http://voicerepublic.com"))))


;; --- setup

(defn- make-frame []
  (frame :title title
         :menubar (menubar
                   :items
                   [(menu :text "File"
                          :items [backup-folder-action
                                  exit-action])
                    (menu :text "Help"
                          :items [help-action])])))

(defn- dimension
  "Returns a dimension `[x :by y]` of a given component, adds options
  x and y to width resp. height if provided."
  [panel & {:keys [x y] :or {x 0 y 0}}]
  (let [dim (.getPreferredSize panel)]
    [(+ x (.-width dim)) :by (+ y (.-height dim))]))

(defn start []
  "Starts the GUI"
  ;; Before any UI is created, tell seesaw to make things look as native
  ;; as possible
  (native!)

  (let [the-frame (make-frame)
        content (content-comp)]
    (invoke-later (-> the-frame pack! show!))

    ;; TODO to add a status bar don't use this but "dock south in mig"
    ;;(config! the-frame :content (border-panel
    ;;                     :north (horizontal-panel
    ;;                             :items [logo (label :text "       ") title])
    ;;                     :south (label :text "Brought to you via voicerepublic.com")
    ;;                     :center main :vgap 5 :hgap 5 :border 5))

    (config! the-frame :content content)

    ;; Set size after everything else is in the frame, otherwise the
    ;; size in Windows will be set to 0x0 anyway.
    (config! the-frame :size (dimension content :y 60))))
