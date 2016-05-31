(defproject vr_logorrhoe "0.1.0-SNAPSHOT"
  :description "VoiceRepublic.com *re-stream* Desktop Client"
  :url "http://voicerepublic.com/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[seesaw "1.4.5"]
                 [com.climate/java.shell2 "0.1.0"]
                 [clj-http "2.1.0"]
                 [org.clojure/clojure "1.8.0"]]
  :main vr-logorrhoe.core

  :repositories [["sonatype" "http://oss.sonatype.org/content/repositories/releases"]
                 ["central" "http://mirrors.ibiblio.org/pub/mirrors/maven2"]])
