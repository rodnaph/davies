
(ns davies.layout
  (:use net.cgrand.enlive-html))

(deftemplate standard "davies/views/layout.html"
  [params]
  [:span#title] (content (:title params))
  [:div.content] (content (:content params)))
