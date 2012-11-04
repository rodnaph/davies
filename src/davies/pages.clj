
(ns davies.pages
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

;; Public
;; ------

(defn index [req]
  (let [index-tx '[:find ?e
                   :where [?e :blog/title]]
        items (db/query index-tx)]
    (html5
      [:head
        [:title "Davies - Datomic Blog"]
        (include-css "/assets/bootstrap-2.0/css/bootstrap.css")]
      [:body
        [:div.container
          [:div.row
            [:div.span12
              [:h1 "My Datomic Blog"]]]
          [:div.row
            [:div.span8
              (list-posts items)]]]])))

