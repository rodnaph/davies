
(ns davies.layout
  (:use hiccup.page))

(defn standard [title & content]
  (html5
   [:head
    [:title (str "Davies - " title)]
    (include-css "/assets/bootstrap-2.0/css/bootstrap.min.css"
                 "/assets/css/main.css")]
   [:body
    [:div.container
     [:div.row
      [:div.span12
       [:h1
        [:a {:href "/"} "My Datomic Blog"]]
       [:p.lead "Simple Datomic Blog"]]]
     content
     [:div.row.footer
      "A simple blog using Clojure and Datomic"]]]))
