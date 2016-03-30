(ns vr-logorrhoe.core
  (:gen-class)
  (:use seesaw.core)
  (:require
   ;; [net.n01se.clojure-jna :as jna]

            [clj-http.client :as client]
            [vr-logorrhoe.audio :refer [record]]))



;; (defn crazy-batshit-from-c [num]
;;   (jna/invoke Integer c/printf "The number given: %d\n" num))


;; (defn -main [& args]
;;   (native!)
;;   (invoke-later
;;     (-> (frame :title "Hello VR",
;;            :content "Hello, VR - this is from Clojure with Love!",
;;            :on-close :exit)
;;      pack!
;;      show!)))

;; (-main)


(comment

(native!)
(def f (frame :title "Get to know Seesaw"))
(-> f pack! show! )
(config f :title)
(defn display [content]
  (config! f :content content)
  content)

(def lbl (label "I'm another label"))

(display lbl)

(config! lbl
         :text "Som3 sw33t t3xt"
         :background :pink
         :foreground "#00f"
         :font "ARIAL-BOLD-36")

(use 'seesaw.font)

(config! lbl :font (font :name :monospaced
                         :style #{:bold :italic}
                        :size 34))

(def b (button :text "Click me"))

(alert "I'm an alert")

(input "What's your favourite color?")

(listen b :action (fn[e] (alert e "Thanks!")))

(listen b :mouse-entered #(config! % :foreground :blue)
          :mouse-exited #(config! % :foreground :black))

(def lb (listbox :model (-> 'seesaw.core ns-publics keys sort)))

(display (scrollable lb))

(selection lb)

(type *1)

(selection! lb 'border-panel)

(listen lb :selection #(println "Selection is " (selection %)))

(*1)

(def field (display (text "This is a text field")))

(text! field "a new value")

(config! field :font "MONOSPACE-PLAIN-38" :background :grey)

(def area (text :multi-line? true
                :font "MONOSPACE-PLAIN-38"
                :text "this
is
a
multi-
line-
text"))

(display area)

(text! area (java.net.URL. "http://clojure.com"))

(display (scrollable area))

(scroll! area :to :top)

(def split (left-right-split (scrollable lb) (scrollable area) :divider-location 1/3))

(display split)

(defn doc-str [s] (-> (symbol "seesaw.core" (name s)) resolve meta :doc))

(listen lb :selection
        (fn[e]
          (when-let [s (selection e)]
            (-> area
                (text! (doc-str s))
                (scroll! :to :top)))))


(def rbs (for [i [:source :doc]]
           (radio :id i :class :type :text (name i))))

(display (border-panel
          :north (horizontal-panel :items rbs)
          :center split
          :vgap 5 :hgap 5 :border 5))

(select f [:JRadioButton])

(def group (button-group))

(config! (select f [:.type]) :group group)

(selection group)

(-> group selection id-of)


(display (vertical-panel :items ["This" "is" "a" "vertical" "stack of" "JLabels"]))

(display (input "Pick a city"
    :choices [{ :name "New York"   :population 8000000 }
              { :name "Ann Arbor"  :population 100000 }
              { :name "Twin Peaks" :population 5201 }]
    :to-string :name))


(display (listbox :model ["jim" "bob" "al"]))


;; End Comment
)


;; (import java.util.Stack)
;; (Stack.)

;; (client/put "http://52.58.65.224/4609"
;;              {
;;               :basic-auth ["source" "thisisagoodpassword"]
;;               :body (clojure.java.io/file "/home/munen/src/voicerepublic_icecast_tests/manual_put/test.ogg")
;;               :headers {
;;                         :user-agent "vr_shout/0.2.0"
;;                         :ice-bitrate "128"
;;                         :content-type "application/ogg"
;;                         :ice-name "VR Server Name"
;;                         :ice-genre "Rock"
;;                         :ice-title "VR Title"
;;                         :ice-url "https://voicerepublic.com"
;;                         :ice-private "0"
;;                         :ice-public "1"
;;                         :ice-description "VR Server Description"
;;                         :ice-audio-info "ice-samplerate=44100;ice-bitrate=128;ice-channels=2"
;;                         }
;;               })


;; (-main)
;; (crazy-batshit-from-c 112423)
