(defproject vr_logorrhoe "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[seesaw "1.4.5"]
                 [clj-http "2.1.0"]
                 [net.n01se/clojure-jna "1.0.0"]
                 [org.clojure/clojure "1.8.0"]]
  :main vr-logorrhoe.core

  :profiles
  {:project/dev { :env {:dev        true
                        :port       3000
                        :nrepl-port 7000}}})
