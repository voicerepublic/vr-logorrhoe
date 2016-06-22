(ns vr-logorrhoe.rabbitmq
  "

  TODO

  https://github.com/rabbitmq/rabbitmq-auth-backend-http

  "
  (:require
   [vr-logorrhoe
    [config :as config :refer [setting]]]
   [langohr.core :as rmq]
   [langohr.channel :as lch]
   [langohr.queue :as lq]
   [langohr.exchange :as le]
   [langohr.consumers :as lc]
   [langohr.basic :as lb]
   [cheshire.core :as json]))

(def ^{:private true :const true}
  type-json "application/json")

(def ^{:private true :const true}
  virtual-host "/devices")

(def ^{:private true :const true}
  exchange-name (str "/device/" (setting :identifier)))

(defn- message-handler
  [ch {:keys [content-type type] :as meta} ^bytes payload]
  (println (format "[consumer] Received a message: %s, content type: %s, type: %s"
                   (String. payload "UTF-8")
                   content-type
                   type))
  (lb/publish ch
              exchange-name
              "done"
              {} ;; empty body
              {:content-type type-json}))


(defn- make-private-queue [channel]
  (.getQueue (lq/declare channel "" {:exclusive false :auto-delete true})))

(defn- start-consumer
  "Starts a consumer in a separate thread"
  [ch queue-name]
  (let [handler (fn [ch metadata ^bytes payload]
                  (println (format "[consumer] %s received a message: %s"
                                   queue-name
                                   (String. payload "UTF-8"))))
        thread  (Thread. (fn []
                           (lc/subscribe ch queue-name handler {:auto-ack true})))]
    (.start thread)))


(defn attach
  []
  (let [connection (rmq/connect)
        channel (lch/open connection)]
    (println (format "[main] Connected. Channel id: %d" (.getChannelNumber channel)))
    (le/declare channel exchange-name "topic")
    (let [queue (make-private-queue channel)]
      (lq/bind channel queue exchange-name)
      (start-consumer ch q))
    (lc/blocking-subscribe channel qname message-handler {:auto-ack true})
    (rmq/close channel)
    (rmq/close connection)))
