
(ns davies.web
  (:use compojure.core
        ring.middleware.reload
        ring.middleware.stacktrace)
  (:require (compojure [handler :as handler]
                       [route :as route])
            (davies [posts :as posts])))

(defroutes app-routes
  (GET "/" [] posts/index)
  (GET "/post/:id" [] posts/show)
  (route/resources "/assets")
  (route/not-found "404..."))

(def app 
  (-> #'app-routes
    (wrap-reload)
    (wrap-stacktrace)
    (handler/site)))

