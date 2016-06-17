(ns vr-logorrhoe.logger)

(defn log [& msg]
  (prn (apply str msg)))
