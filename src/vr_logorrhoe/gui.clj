(ns vr-logorrhoe.gui
  (:gen-class)
  (:use seesaw.core
        seesaw.font))

;; Helpers
(native!)

(def exit-action (action
                  :handler (fn [e] (.dispose (to-frame e)))
                  :name "Exit"
                  :tip  "Close this window"))

(def f (frame :title "VR - *re:stream*"
              :menubar
              (menubar :items
                       [(menu :text "File" :items [exit-action])
                        (menu :text "Help" :items [exit-action])])))

(defn display [content]
  (config! f :content content)
  content)


(defn start []
  (-> f pack! show! )
  (config f :title)

  (let [logo (label
              :icon (java.io.File. "resources/logo.png"))
        title (label
             :text "VR - *re:stream* - Venue Title"
             :background :lightgrey
             :foreground "#00f"
             :font (font :name :monospaced
                         :style #{:bold}
                         :size 34))
        b   (button :text "Start")
        audio-inputs (listbox :model ["Microphone" "Line-In"])
        split (left-right-split (scrollable audio-inputs) b :divider-location 1/3)]

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

    (listen b :action (fn[e]
                        (alert e "Started!")
                        (config! b :text "Stop")))))

(start)
