
(ns davies.posts
  (:refer-clojure :exclude [comment])
  (:use net.cgrand.enlive-html)
  (:require [ring.util.response :as response]
            [davies.db :as db]
            [davies.layout :as layout]))

(defn- ^{:doc "Fetch all comments for a post"}
  comments-for [post-id]
  (let [comments-tx '[:find ?e
                      :in $ ?i
                      :where [?e :comment/post ?i]]]
    (db/entities (db/query comments-tx post-id))))

(defn- ^{:doc "Inserts a comment into the database."}
  insert-comment [{:keys [id author message]}]
  (let [data-tx {:db/id #db/id[db.part/user]
                 :comment/author author
                 :comment/message message
                 :comment/created-at (java.util.Date.)
                 :comment/post (Long/parseLong id)}]
    (db/transact [data-tx])))

(defn- insert-post [{:keys [title body]}]
  (let [data-tx {:db/id #db/id[db.part/user]
                 :blog/title title
                 :blog/body body
                 :blog/created-at (java.util.Date.)}]
    (db/id (db/transact [data-tx]))))

(defn ^{:doc "Find posts for the front page"}
  find-posts []
  (let [index-tx '[:find ?e
                   :where [?e :blog/title]]]
	(db/entities (db/query index-tx))))

(defn- redirect [flash url & url-params]
  (merge {:flash flash}
    (response/redirect
      (apply format (cons url url-params)))))

;; Public
;; ------

(defn show [{:keys [params] :as req}]
  (let [id (Long/parseLong (:id params))
        post (db/entity id)]
    (layout/standard
     {:title (:blog/title post)
      :flash (:flash req)
      :content (layout/post post (comments-for id))})))

(defn comment [{:keys [params]}]
  (insert-comment params)
  (redirect "Your comment was posted!"
            "/posts/%s#comments"
            (:id params)))

(defn index [req]
  (layout/standard
   {:title "Home"
    :content (layout/index (find-posts))}))

(defn new [req]
  (layout/standard
   {:title "Create new post"
    :content (layout/new)}))

(defn create [{:keys [params]}]
  (let [id (insert-post params)]
    (redirect "Your entry was posted!"
              "/posts/%s"
              id)))
