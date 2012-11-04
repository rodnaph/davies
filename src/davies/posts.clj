
(ns davies.posts
  (:refer-clojure :exclude [comment])
  (:use hiccup.form)
  (:require [ring.util.response :as response]
            [davies.db :as db]
            [davies.layout :as layout]))

(defn- ^{:doc "Turn a post ID into a post summary"}
  post2summary [id]
  (let [post (db/entity id)]
    [:div.well
      [:h2
        [:a {:href (format "/posts/%s" (:db/id post))}
          (:blog/title post)]]
      [:div.snippet
        (:blog/body post)]]))

(defn- list-posts [posts]
  (map (comp post2summary first) posts))

(defn- comment-form [id]
  (let [url (format "/posts/%s/comments" id)]
    (form-to {:class "form-horizontal"} [:post url]
             [:div.control-group
              [:label.control-label "Message:"]
              [:div.controls
               (text-area "message")]]
             [:div.form-actions
              (submit-button {:class "btn btn-primary"} "Post Comment")])))

(defn- ^{:doc "Turn a comment ID into a rendered comment"}
  id2comment [id]
  (let [comment (db/entity id)]
    [:li (:comment/message comment)]))

(defn- ^{:doc "Fetch all comments for a post"}
  comments-for [post-id]
  (let [comments-tx '[:find ?e
                      :in $ ?i
                      :where [?e :comment/post ?i]]]
    (db/query comments-tx post-id)))

;; Public
;; ------

(defn show [{:keys [params]}]
  (let [id (bigint (:id params))
        post (db/entity id)]
    (layout/standard (:blog/title post)
      [:div.row
       [:div.span12
        [:div.well
         [:h2 (:blog/title post)]
         (:blog/body post)]]]
      [:div.row
       [:div.span12
        [:ul
         (map (comp id2comment first)
              (comments-for id))]
        [:div.well
         [:h2 "Post Comment"]
         [:p "To post a comment, just enter your message below and click submit."]
         (comment-form id)]]])))

(defn comment [{:keys [params]}]
  (let [post-id (bigint (:id params))
        data-tx {:db/id #db/id[db.part/user]
                 :comment/message (:message params)
                 :comment/post post-id}]
    (try
      (db/transact [data-tx])
      (catch Exception e
        (println params)
        (.getMessage e))))
  (response/redirect "/"))

(defn index [req]
  (let [index-tx '[:find ?e
                   :where [?e :blog/title]]
        items (db/query index-tx)]
    (layout/standard "Home"
      [:div.row
       [:div.span8
        (list-posts items)]
       [:div.span4
        [:div.well
         [:h3 "Sidebar"]]]])))

