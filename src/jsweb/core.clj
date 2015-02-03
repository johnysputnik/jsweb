(ns jsweb.core
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [compojure.core :as compojure :refer [GET ANY defroutes]]
            [ring.middleware.json :as json]
            [cemerick.friend :as friend]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]
            (cemerick.friend [workflows :as workflows]
                             [credentials :as creds])))

; A dummy set of users
;
(def users {"root" {:username "root"
                    :password (creds/hash-bcrypt "password")
                    :roles #{::admin}}
            "user" {:iusername "user"
                    :password (creds/hash-bcrypt "password2")
                    :roles #{::user}}})



(defn user-routes [user-id]
  (GET "/profile" request "profile")
  (GET "/posts" request "posts"))


;           (compojure/context "/user/:user-id" [user-id]
;          (user-routes user-id))
(defroutes app
           (compojure/context "/user" request
                              (friend/wrap-authorize user-routes #{::user}))
           (GET "/" request "landing page")
           (GET "/login" request "login")
           (friend/logout (ANY "/logout" request
                               (ring.util.response/redirect "/")))
           (route/not-found "not found"))


(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (handler/site #'app) {:port port :join? false})))


;; (.stop server)
;; (def server (-main))