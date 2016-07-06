(ns vr-logorrhoe.logging
  "

  It might be overkill but this namespace covers only the initial
  setup of timbre logging. Additionally it documents the refers that
  need to be setup in other namespaces to do some logging. This is a
  good start:

    [taoensso.timbre :as timbre :refer [info]]

  Timbre by default logs with level :debug to the console. This is
  also the case for this app until our setup function is called.

  The log levels are

    :trace :debug :info :warn :error :fatal :report

  More about timbre

    https://github.com/ptaoussanis/timbre

  "
  (:require
   [vr-logorrhoe
    [config :as config :refer [setting]]]
   [taoensso.timbre :as timbre
    :refer [log trace debug info warn error fatal report
            logf tracef debugf infof warnf errorf fatalf reportf
            spy get-env log-env]]
   [taoensso.timbre.profiling :as profiling
    :refer [pspy p defnp profile]]
   [taoensso.timbre.appenders.core :as appenders]))


(defn setup! []
  (timbre/set-config!
   {:level (setting :log-level)
    :appenders
    {:println (appenders/println-appender {:stream :auto})
     :spit (appenders/spit-appender {:fname (setting :log-file)})}}))
