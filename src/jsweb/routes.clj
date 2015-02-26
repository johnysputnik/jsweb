;; routing with compojure

(ns jsweb.routes
  (:require [jsweb.templates :as templates]
            [compojure.route :as route]
            [compojure.core :refer [GET ANY defroutes]]))

;; top level routing

(defroutes app
           (GET "/" [] (templates/home {:title "jSolutions Ltd"}))
           (GET "/articles" [] (templates/articles {:title "article"}))
           (GET "/article" [] (templates/article {:title "latest articles"}))
           (GET "/about" [] (templates/about {:title "about jSolutions"}))
           (GET "/contact" [] (templates/contact {:title "contact jSolutions"}))
           (route/resources "/")
           (route/not-found "not found"))
