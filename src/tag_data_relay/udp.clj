(ns tag-data-relay.udp
  (:require [taoensso.timbre :refer [infof]])
  (:import (java.net InetSocketAddress DatagramPacket DatagramSocket)))

(defonce udp-server (atom nil))
(def udp-port 5500)

(defn message [text ip port]
  (DatagramPacket. (.getBytes text)
                   (.length text)
                   (InetSocketAddress. ip port)))

(defn send-update
  [id x y ip port]
  (.send @udp-server (message (str "{ id: " id ", x: " x ", y: " y " }") ip port)))

(defn create-udp-server
  []
  (DatagramSocket. udp-port))

(defn start-udp-server
  []
  (infof "Start udp server on port %s" udp-port)
  (reset! udp-server (create-udp-server)))

(defn stop-udp-server
  []
  (infof "Stop udp server on port %s" udp-port)
  (.close @udp-server)
  (reset! udp-server nil))

