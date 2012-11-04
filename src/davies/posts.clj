
(ns davies.posts
  (:use (hiccup core page))
  (:require [davies.db :as db]))

(defn- post2summary [id]
  (let [post (db/entity id)]
    [:li
      [:h2
        [:a {:href (format "/post/%s" (:db/id post))}
          (:blog/title post)]]
      [:div.snippet
        (:blog/body post)]]))

(defn- list-posts [posts]
  [:ul.posts.posts-summary
   (map (comp post2summary first) posts)])

(defn layout [title & content]
  (html5
   [:head
    [:title (str "Davies - " title)]
    (include-css "/assets/bootstrap-2.0/css/bootstrap.css"
                 "/assets/css/main.css")]
   [:body
    [:div.container
     [:div.row
      [:div.span12
       [:h1 "My Datomic Blog"]]]
     content
     [:div.row.footer
      "Davies - A simple blog using Clojure and Datomic"]]]))

;; Public
;; ------

(defn show [{:keys [params]}]
  (let [id (bigint (:id params))
        post (db/entity id)]
    (layout (:blog/title post)
      [:div.row
       [:div.span12
        [:div
         [:a {:href "/"} "Home"]]
        (:blog/body post)]])))

(defn index [req]
  (let [index-tx '[:find ?e
                   :where [?e :blog/title]]
        items (db/query index-tx)]
    (layout "Home"
      [:div.row
       [:div.span8
        (list-posts items)]])))

