(ns tag-data-relay.route.api
  (:require [clojure.spec.alpha :as s]
            [taoensso.timbre :refer [infof]]
            [tag-data-relay.udp :as udp-server]))

(s/def ::id string?)
(s/def ::x int?)
(s/def ::y int?)
(s/def ::syncX float?)
(s/def ::syncY float?)
(s/def ::syncZ float?)
(s/def ::ip string?)

(s/def ::update-request (s/keys :req-un [::id ::x ::y]))
(s/def ::sync-request (s/keys :req-un [::id ::syncX ::syncY ::syncZ]))
(s/def ::subscribe-request (s/keys :req-un [::ip]))

(defonce udp-clients (atom {}))

(defn gen-port
  []
  (let [p (+ 5000 (rand-int 1000))
        clients @udp-clients]
    (loop [port p]
      (if (some #{port} clients)
        (recur (+ 5000 (rand-int 1000)))
        p))))

(defn send-updates
  [id x y syncX, syncY, syncZ]
  (doseq [port (vals @udp-clients)
          ip   (keys @udp-clients)]
    (println "Send update to" ip port)
    (udp-server/send-update id x y syncX syncY syncZ ip port)))

(def route
  ["/api"
   ["/update"
    {:post
     {:parameters {:query ::update-request}
      :handler    (fn [{{{:keys [id x y]} :query} :parameters}]
                    (infof "Received: %s %d %d" id x y)
                    (send-updates id x y 0 0 0)
                    {:status 200
                     :body {:received [id x y]}})}}]
   
   ["/sync"
    {:post
     {:parameters {:query ::sync-request}
      :handler    (fn [{{{:keys [id syncX syncY syncZ]} :query} :parameters}]
                    (infof "Received: %s %f %f %f" id syncX syncY syncZ)
                    (send-updates id -1 -1 syncX syncY syncZ)
                    {:status 200
                     :body {:received [id syncX syncY syncZ]}})}}]

   ["/subscribe"
    {:post
     {:parameters {:query ::subscribe-request}
      :handler    (fn [{{{:keys [ip]} :query} :parameters}]
                    (infof "Subscription request received from %s" ip)
                    (let [port (gen-port)]
                      (swap! udp-clients assoc ip port)
                      {:status 200
                       :body {:port port
                              :ip ip}}))}}]
   ["/unsubscribe"
    {:post
     {:parameters {:query ::subscribe-request}
      :handler    (fn [{{{:keys [ip]} :query} :parameters}]
                    (infof "Unsubscribe request received from %s" ip)
                    (swap! udp-clients dissoc ip)
                    (println "Current clients" @udp-clients)
                    {:status 200
                     :body {:ip ip}})}}]])