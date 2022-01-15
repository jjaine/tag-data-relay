(ns tag-data-relay.core
  (:require tag-data-relay.server
            ; load mount state
            [mount.core :as mount]
            [taoensso.timbre :refer [info]])
  (:gen-class))

(defn -main
  "The entry point for uberjar."
  [& _args]
  (info "Starting server...")
  (mount/start)
  (deref (promise)))