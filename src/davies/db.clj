
(ns davies.db
  (:require [datomic.api :as d]))

(def schema-tx [

  ;; blog authors

  { :db/id #db/id[:db.part/db]
   :db/ident :author/name
   :db/cardinality :db.cardinality/one
   :db/valueType :db.type/string
   :db/doc "The name of a blog author"
   :db.install/_attribute :db.part/db }

  ;; blog posts

  { :db/id #db/id[:db.part/db]
   :db/ident :blog/title
   :db/cardinality :db.cardinality/one
   :db/valueType :db.type/string
   :db/doc "The title of a blog post"
   :db/fulltext true
   :db.install/_attribute :db.part/db }

  { :db/id #db/id[:db.part/db]
   :db/ident :blog/body
   :db/cardinality :db.cardinality/one
   :db/valueType :db.type/string
   :db/doc "The body of a blog post"
   :db/fulltext true
   :db.install/_attribute :db.part/db }

  { :db/id #db/id[:db.part/db]
   :db/cardinality :db.cardinality/one
   :db/ident :blog/author
   :db/valueType :db.type/ref
   :db/doc "The author of a blog post"
   :db.install/_attribute :db.part/db }

  { :db/id #db/id[:db.part/db]
   :db/cardinality :db.cardinality/one
   :db/ident :blog/created-at
   :db/valueType :db.type/instant
   :db/doc "The time a blog entry was posted"
   :db.install/_attribute :db.part/db }

  ;; blog comments

])

(def uri "datomic:free://localhost:4334/davies")

(defonce ^{:dynamic true} cnn (atom nil))

;; Public
;; ------

(defn connection []
  @cnn)

(defn database []
  (d/db (connection)))

(defn query [tx]
  (d/q tx (database)))

(defn entity [id]
  (d/entity (database) id))

(defn init []
  (d/create-database uri)
  (reset! cnn (d/connect uri))
  (d/transact (connection) schema-tx))

