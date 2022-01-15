(defproject tag-data-relay "0.1.0-SNAPSHOT"
  :description "Tag data relay"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [metosin/reitit "0.5.4"]
                 [com.taoensso/timbre "5.1.2"]
                 [mount "0.1.16"]
                 [tolitius/mount-up "0.1.3"]
                 [ring/ring-jetty-adapter "1.9.4"]
                 [aero "1.1.6"]]
  :main ^:skip-aot tag-data-relay.core
  :min-lein-version "2.0.0"
  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "1.0.0"]]
                   :repl-options {:init-ns dev
                                  :init    (start)}
                   :source-paths ["dev" "src" "test"]}
             :uberjar {:aot      :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
