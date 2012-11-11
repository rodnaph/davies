
(ns davies.layout
  (:use net.cgrand.enlive-html))

(defsnippet post-summary "davies/views/index.html" [:.summary]
  [post]
  [:a.title] (do-> (content (:blog/title post))
  				   (set-attr :href
                             (format "/posts/%d"
                                     (:db/id post)))))

(defsnippet post-comment "davies/views/post.html" [:.comment]
  [comment]
  [:.author] (content (:comment/author comment))
  [:p] (content (:comment/message comment)))

;; Public
;; ------

(deftemplate standard "davies/views/layout.html"
  [params]
  [:span#title] (content (:title params))
  [:div.content] (content (:content params)))

(defsnippet new "davies/views/new.html" [:.row]
  [])

(defsnippet post "davies/views/post.html" [:.post]
  [post comments]
  [:.title] (content (:blog/title post))
  [:.body] (html-content (:blog/body post))
  [:.comments] (content (map #(post-comment %) comments))
  [:form] (set-attr :action
                    (format "/posts/%d/comments"
                            (:db/id post))))

(defsnippet index "davies/views/index.html" [:div.row]
  [posts]
  [:ul.posts] (content (map #(post-summary %) posts)))
