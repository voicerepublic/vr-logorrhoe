(ns vr-logorrhoe.utils)

(defn get-declared-methods [obj]
  "Get declared methods on `obj` through Java Reflection"
  (map #(.toString %)
       (seq (.getDeclaredMethods (.getClass obj)))))

;; (defn crazy-batshit-from-c [num]
;;   (jna/invoke Integer c/printf "The number given: %d\n" num))
;; (crazy-batshit-from-c 112423)
