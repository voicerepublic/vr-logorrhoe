(ns vr-logorrhoe.utils
  (:import [java.io BufferedReader InputStreamReader]))

(defn get-declared-methods [obj]
  "Get declared methods on `obj` through Java Reflection"
  (map #(.toString %)
       (seq (.getDeclaredMethods (.getClass obj)))))

(defn print-input-stream [input-stream]
  "Helper function to print an InputStream to the REPL"
  (prn "Printing input-stream: " input-stream)
  (try
    (let [in (new BufferedReader (new InputStreamReader input-stream))]
      (loop []
        (let [line (.readLine in)]
          (if (not (= line nil))
            (prn line)
            (recur))))
      (.close input-stream))
    (catch Exception e
      (println "Caught: " e))))

;; (print-input-stream (new java.io.FileInputStream "/tmp/foo"))

;; (defn crazy-batshit-from-c [num]
;;   (jna/invoke Integer c/printf "The number given: %d\n" num))
;; (crazy-batshit-from-c 112423)
