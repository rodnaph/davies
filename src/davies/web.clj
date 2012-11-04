
(ns davies.web
  (:use compojure.core
        ring.middleware.reload
        ring.middleware.stacktrace)
  (:require (compojure [handler :as handler]
                       [route :as route])
            (davies [pages :as pages])))

(defroutes app-routes
  (GET "/" [] pages/index)
  (route/resources "/assets")
  (route/not-found "404..."))

(def app 
  (-> #'app-routes
    (wrap-reload)
    (wrap-stacktrace)
    (handler/site)))

