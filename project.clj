
(defproject davies "0.0.1"
  :description "Simple blog with Datomic"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [compojure "1.1.3"]
                 [ring/ring-jetty-adapter "1.1.2"]
                 [ring/ring-devel "1.1.6"]
                 [enlive "1.0.1"]
                 [com.datomic/datomic-free "0.8.3551"]]
  :main davies.core)

