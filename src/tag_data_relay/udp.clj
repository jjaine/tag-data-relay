(ns tag-data-relay.udp
  (:require [taoensso.timbre :refer [infof]])
  (:import (java.net InetAddress DatagramPacket DatagramSocket)))

(defonce udp-server (atom nil))
(def udp-port 5500)

(defn localhost
  []
  (. InetAddress getLocalHost))

(defn message [text port]
  (DatagramPacket. (.getBytes text)
                   (.length text)
                   (localhost)
                   port))

(defn send-update
  [id x y port]
  (.send @udp-server (message (str "{ id: " id ", x: " x ", y: " y " }") port)))

(defn create-udp-server
  []
  (DatagramSocket. udp-port))

(defn start-udp-server
  []
  (infof "Start udp server on port %s" udp-port)
  (reset! udp-server (create-udp-server)))

(defn stop-udp-server
  []
  (.close @udp-server)
  (reset! @udp-server nil))

