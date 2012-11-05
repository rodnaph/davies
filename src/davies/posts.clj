
(ns davies.posts
  (:refer-clojure :exclude [comment])
  (:use hiccup.form)
  (:require [ring.util.response :as response]
            [davies.db :as db]
            [davies.layout :as layout]))

(defn- ^{:doc "Turn a post ID into a post summary"}
  id2summary [id]
  (let [post (db/entity id)]
    [:div.well
      [:h2
        [:a {:href (format "/posts/%s" (:db/id post))}
          (:blog/title post)]]
      [:div.snippet
        (:blog/body post)]]))

(defn- ^{:doc "Turn a comment ID into a rendered comment"}
  id2comment [id]
  (let [comment (db/entity id)]
    [:li (:comment/message comment)]))

(defn- row [title control]
  [:div.control-group
   [:label.control-label title]
   [:div.controls control]])

(defn- comment-form [id]
  (let [url (format "/posts/%s/comments" id)]
    (form-to {:class "form-horizontal"} [:post url]
             (row "Name:" (text-field "author"))
             (row "Message:" (text-area "message"))
             [:div.form-actions
              (submit-button {:class "btn btn-primary"} "Post Comment")])))

(defn- ^{:doc "Fetch all comments for a post"}
  comments-for [post-id]
  (let [comments-tx '[:find ?e
                      :in $ ?i
                      :where [?e :comment/post ?i]]]
    (db/query comments-tx post-id)))

;; Public
;; ------

(defn show [{:keys [params]}]
  (let [id (Long/parseLong (:id params))
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
  (let [post-id (Long/parseLong (:id params))
        data-tx {:db/id #db/id[db.part/user]
                 :comment/author (:author params)
                 :comment/message (:message params)
                 :comment/created-at (java.util.Date.)
                 :comment/post post-id}
        post-url (format "/posts/%s#comments" post-id)]
    (db/transact [data-tx])
    (response/redirect post-url)))

(defn index [req]
  (let [index-tx '[:find ?e
                   :where [?e :blog/title]]
        items (db/query index-tx)]
    (layout/standard "Home"
      [:div.row
       [:div.span8
        (map (comp id2summary first) items)]
       [:div.span4
        [:div.well
         [:h3 "Sidebar"]]]])))

