(ns vr-logorrhoe.utils
  (:require [clojure.java.io :as io]
            [vr-logorrhoe.logger :refer [log]])
  (:import [java.io BufferedReader InputStreamReader]))

(defn get-declared-methods [obj]
  "Get declared methods on `obj` through Java Reflection"
  (map #(.toString %)
       (seq (.getDeclaredMethods (.getClass obj)))))

(defn print-input-stream [input-stream]
  "Helper function to print an InputStream to the REPL"
  (log "Printing input-stream: " input-stream)
  (try
    (let [in (new BufferedReader (new InputStreamReader input-stream))]
      (loop []
        (let [line (.readLine in)]
          (if (not (= line nil))
            (log line)
            (recur))))
      (.close input-stream))
    (catch Exception e
      (log "Caught: " e))))

(defn path-exists? [path]
  (.exists (io/file path)))

(defn folder-exists? [folder]
  (and (path-exists? folder)
       (.isDirectory (io/file folder))))

(defn create-folder [folder]
  (.mkdir (java.io.File. folder)))

(defn delete-folder
  "Deletes the folder 'folder'. Optionally deletes 'folder'
  recursively if the second argument is set to `true`"
  ([folder]
   (let [f (java.io.File. folder)]
     (when (.isDirectory f)
       (.delete f))))
  ([folder recursive]
   (let [f (java.io.File. folder)]
     (when (and
            (= recursive true)
            (.isDirectory f))
       (let [file-list (.listFiles f)]
         (doall
          (map #(.delete %) file-list))
         (.delete f))))))

(defn conj-path [& args]
  "Takes a list of folders and joins them in regard to the current
  Operating System. Returns the resulting path as a String."
  (str (apply io/file args)))

(defn index-of [item coll]
  "Returns the index of an item in a collection"
  (count (take-while (partial not= item) coll)))

(defn parse-int [s]
  "Casts a String into an Int"
  (. Integer (parseInt s)))
