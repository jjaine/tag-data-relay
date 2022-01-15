(ns tag-data-relay.server-test
  (:require [clojure.test :refer :all]
            [clojure.string :as s]
            [tag-data-relay.server :refer :all]
            [mount.core :as mount]))

(defn mount [f]
  (mount/start)
  (f)
  (mount/stop))

(use-fixtures :once mount)

(def app (build-app))

(deftest health-route-test
  (testing "Health route test"
    (is (= {:status 200, :body "ok"}
           (app {:request-method :get, :uri "/health"})))
    (is (= {:status 404, :body "", :headers {}}
           (app {:request-method :get, :uri "wrong-url"})))))

(deftest subscribe-route-test
  (testing "Subscribe route test"
    (let [ip       "192.168.1.1"
          response (app {:request-method :post
                         :uri            "/api/subscribe"
                         :query-params   {"ip" ip}})
          status   (:status response)
          body     (slurp (:body response))]
      (is (= 200 status))
      (is (s/includes? body "port"))
      (is (s/includes? body "ip"))
      (is (s/includes? body ip)))))

(deftest update-route-test
  (testing "Update route test"
    (let [id "test"
          x 12
          y 31
          response (app {:request-method :post
                         :uri            "/api/update"
                         :query-params   {"id" id
                                          "x"  x
                                          "y"  y}})
          status   (:status response)
          body     (slurp (:body response))]
      (is (= 200 status))
      (is (= (str "{\"received\":[\"" id "\"," x "," y "]}") 
             body)))))