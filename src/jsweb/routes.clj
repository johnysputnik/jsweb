;; routing with compojure

(ns jsweb.routes
  (:require [jsweb.database :as db]
            [jsweb.templates :as templates]
            [compojure.route :as route]
            [compojure.core :refer [GET ANY defroutes]]
            [ring.util.codec :as rcodec]))

;; top level routing

(defroutes app
           (GET "/" [] (templates/home {:data (db/get-all 4)}))
           (GET "/articles" [tag] (templates/articles {:data (if (empty? tag)
                                                               (db/get-all)
                                                               (db/get-all-with-tag (rcodec/url-decode tag)))}))
           (GET "/article" [title] (templates/article {:data (db/get-post 
                                                             (rcodec/url-decode title))}))
           (GET "/about" [] (templates/about {:title "jSolutions Ltd"}))
           (GET "/contact" [] (templates/contact {:title "jSolutions Ltd"}))
		   (GET "/reload" [] 
			 (db/reload-posts "./doc/posts")
			 ("done"))
           (route/resources "/")
           (route/not-found "page not found"))
