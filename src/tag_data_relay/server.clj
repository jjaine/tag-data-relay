(ns tag-data-relay.server
  (:require [tag-data-relay.config :as config]
            [tag-data-relay.route.api :as route.api]
            [tag-data-relay.route.health :as route.health]
            [tag-data-relay.udp :as udp-server]
            [mount.core :as mount :refer [defstate]]
            [muuntaja.core :as m]
            [reitit.ring :as ring]
            [reitit.coercion.spec]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [ring.adapter.jetty :as jetty]
            [taoensso.timbre :refer [infof]]))

(def routes
  [route.api/route
   route.health/route])

(defn build-middlewares
  "Helper to build the middleware depending on the configuration"
  []
  [;; query-params & form-params
   parameters/parameters-middleware
     ;; content-negotiation
   muuntaja/format-negotiate-middleware
     ;; encoding response body
   muuntaja/format-response-middleware
     ;; decoding request body
   muuntaja/format-request-middleware
     ;; coercing response bodys
   coercion/coerce-response-middleware
     ;; coercing request parameters
   coercion/coerce-request-middleware])

(defn build-app
  "Create the server"
  []
  (ring/ring-handler
   (ring/router
    routes
    {:data {:muuntaja m/instance
            :coercion reitit.coercion.spec/coercion
            :middleware (build-middlewares)}})
   (ring/routes
    (ring/create-default-handler))))

(defn- start [{:keys [port]}]
  (infof "Start server in port %s" port)
  (jetty/run-jetty (build-app)
                   {:port port, :join? false, :async true})
  (udp-server/start-udp-server))

(defstate server
  :start (start config/config)
  :stop  (when server
           (.stop server)))
