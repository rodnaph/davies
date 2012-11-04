
(ns davies.core
  (:require [davies.db :as db]
            [davies.web :as web]
            [ring.adapter.jetty :as jetty]))

(defn start []
  (db/init)
  (jetty/run-jetty
    web/app
    {:port 1234}))

(defn -main []
  (start))

