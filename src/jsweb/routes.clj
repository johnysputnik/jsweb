;; routing with compojure

(ns jsweb.routes
  (:require [jsweb.templates :as templates]
            [compojure.route :as route]
            [compojure.core :refer [GET ANY defroutes]]))

;; top level routing

(defroutes app
           (GET "/" [] (templates/home))
           (route/resources "/")
           (route/not-found "not found"))
