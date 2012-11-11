
(ns davies.web
  (:use compojure.core
        ring.middleware.reload
        ring.middleware.flash
        ring.middleware.stacktrace)
  (:require (compojure [handler :as handler]
                       [route :as route])
            (davies [posts :as posts])))

(defroutes app-routes
  (GET "/" [] posts/index)
  (GET "/new" [] posts/new)
  (POST "/new" [] posts/create)
  (GET "/posts/:id" [] posts/show)
  (POST "/posts/:id/comments" [] posts/comment)
  (route/resources "/assets")
  (route/not-found "404..."))

(def app 
  (-> #'app-routes
    (wrap-reload)
    (wrap-flash)
    (wrap-stacktrace)
    (handler/site)))
