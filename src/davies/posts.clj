
(ns davies.posts
  (:refer-clojure :exclude [comment])
  (:use net.cgrand.enlive-html)
  (:require [ring.util.response :as response]
            [davies.db :as db]
            [davies.layout :as layout]))

(defn ^{:doc "Convert entity results to a sequence of entities"}
  to-entities [col]
  (map (comp db/entity first) col))

(defn- ^{:doc "Fetch all comments for a post"}
  comments-for [post-id]
  (let [comments-tx '[:find ?e
                      :in $ ?i
                      :where [?e :comment/post ?i]]]
    (to-entities (db/query comments-tx post-id))))

(defn- ^{:doc "Inserts a comment into the database."}
  insert-comment [{:keys [id author message]}]
  (let [data-tx {:db/id #db/id[db.part/user]
                 :comment/author author
                 :comment/message message
                 :comment/created-at (java.util.Date.)
                 :comment/post (Long/parseLong id)}]
    (db/transact [data-tx])))

(defn ^{:doc "Find posts for the front page"}
  find-posts []
  (let [index-tx '[:find ?e
                   :where [?e :blog/title]]]
	(to-entities (db/query index-tx))))

(defsnippet tpl-comment "davies/views/comment.html" [:.comment]
  [comment]
  [:.author] (content (:comment/author comment))
  [:p] (content (:comment/message comment)))

(defsnippet tpl-show "davies/views/post.html" [:.post]
  [post comments]
  [:.title] (content (:blog/title post))
  [:.body] (html-content (:blog/body post))
  [:.comments] (content (map #(tpl-comment %) comments))
  [:form] (set-attr :action
                    (format "/posts/%d/comments"
                            (:db/id post))))

(defsnippet tpl-summary "davies/views/summary.html" [:.summary]
  [post]
  [:a.title] (do-> (content (:blog/title post))
  				   (set-attr :href
                             (format "/posts/%d"
                                     (:db/id post)))))

(defsnippet tpl-index "davies/views/index.html" [:div.row]
  [posts]
  [:ul.posts] (content (map #(tpl-summary %) posts)))

;; Public
;; ------

(defn show [{:keys [params]}]
  (let [id (Long/parseLong (:id params))
        post (db/entity id)]
    (layout/standard
     {:title (:blog/title post)
      :content (tpl-show post (comments-for id))})))

(defn comment [{:keys [params]}]
  (insert-comment params)
  (response/redirect
    (format "/posts/%s#comments" (:id params))))

(defn index [req]
  (layout/standard
   {:title "Home"
    :content (tpl-index (find-posts))}))
