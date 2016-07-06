(ns vr-logorrhoe.utils
  (:require [clojure.java.io :as io]
            [taoensso.timbre :as timbre :refer (info)])
  (:import [java.io BufferedReader InputStreamReader]
           [java.util Properties]))

(def util-app-state (atom {}))

(defn set-logger-path [path]
  "Takes a path to where the log files should reside"
  (swap! util-app-state assoc :logger-path path))

(defn log [& msg]
  "Prints a message to stdout, also writes to a log file"
  (let [log-msg (apply str msg)]
    (info log-msg)))

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

(defn copy-file [source-path dest-path]
  "Copies a file from `source-path` to `dest-path`"
  (io/copy (io/file source-path) (io/file dest-path)))

(defn copy-file-from-resource [resource-path dest-path]
  "Copies a file from the `resources` folder within a jar file. This
  also works for binary files. This isn't possible using the regular
  tools, because within the jar `clojure.java.io/resource` is
  returning a URL which cannot be consumed by `java.io.File`. `spit`
  and `slurp` would work, but they do not work on binary files."
  (let [src (ClassLoader/getSystemResourceAsStream resource-path)]
    (io/copy src (java.io.File. dest-path))))

(defn die [& args]
  (apply println args)
  (System/exit 1))

(defn project-namespace []
  "Returns `vr-logorrhoe` for the ns `vr-logorrhoe.utils`"
  (first (clojure.string/split (str (ns-name *ns*)) #"\.")))

(defn project-version []
  (let [namespace (project-namespace)]
    (-> (doto (Properties.)
          (.load (-> "META-INF/maven/%s/%s/pom.properties"
                     (format namespace namespace)
                     (io/resource)
                     (io/reader)))))
      (.get "version")))

(defn- generate-uuid []
  (str (java.util.UUID/randomUUID)))

(defn- user-name []
  (or (System/getProperty "user.name") "noname"))

(defn- host-name []
  (or (.. java.net.InetAddress getLocalHost getHostName) "unknown"))

(defn generate-identifier []
  (str (user-name) "@" (host-name) ":" (generate-uuid)))
