(ns vr-logorrhoe.rabbitmq
  "

  TODO

  https://github.com/rabbitmq/rabbitmq-auth-backend-http

  "
  (:require
   [langohr.core :as rmq]
   [langohr.channel :as lch]
   [langohr.queue :as lq]
   [langohr.exchange :as lx]
   [langohr.consumers :as lc]
   [langohr.basic :as lb]
   [cheshire.core :as json]))

(def ^{:private true :const true}
  type-json "application/json")

(def ^{:private true :const true}
  virtual-host "/devices")

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

(defn attach
  []
  (let [connection (rmq/connect)
        channel (lch/open connection)]
    (println (format "[main] Connected. Channel id: %d" (.getChannelNumber channel)))
    (lx/declare channel exchange-name "topic")
    (lq/declare channel qname {:exclusive false :auto-delete true})
    (lc/blocking-subscribe channel qname message-handler {:auto-ack true})
    (rmq/close channel)
    (rmq/close connection)))
