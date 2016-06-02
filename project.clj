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

  :repositories [["java.net" "http://download.java.net/maven/2"]
                 ["sonatype" {:url "http://oss.sonatype.org/content/repositories/releases"
                              ;; If a repository contains releases only setting
                              ;; :snapshots to false will speed up dependencies.
                              :snapshots false
                              ;; Disable signing releases deployed to this repo.
                              ;; (Not recommended.)
                              :sign-releases false
                              ;; You can also set the policies for how to handle
                              ;; :checksum failures to :fail, :warn, or :ignore.
                              :checksum :fail
                              ;; How often should this repository be checked for
                              ;; snapshot updates? (:daily, :always, or :never)
                              :update :always
                              ;; You can also apply them to releases only:
                              :releases {:checksum :fail :update :always}}]])
