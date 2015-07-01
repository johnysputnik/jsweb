(ns jsproto.dragon.web
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [compojure.core :refer [GET POST defroutes]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.json :as json]
            [environ.core :refer [env]]
            [jsproto.dragon.engine :as engine])
  (:gen-class))

;; curl -X POST http://localhost:8082
;;
(defn dragon-info
  []
  {:status 200
   :body {:site "dragon virtual coach"
          :version "0.0.1"}})

;; curl -X POST http://localhost:8082/plan \
;;  --data '{"name":"John"}' \
;;  --header "Content-type:application/json"
;;
(defn dragon-plan
  [request]
  {:status 200
    :body {:app "dragon virtual coach"
           :version "0.0.1"
           :rider (or (get-in request [:body :name])
                       "Unknown")
           :plan (engine/run-rules)}})

(defroutes app-routes
           (POST "/" [] (dragon-info))
           (POST "/plan" request (dragon-plan request))
           (route/resources "/")
           (route/not-found "Not Found"))

(def app
  (-> (handler/site app-routes)
      (json/wrap-json-body {:keywords? true})
      json/wrap-json-response))

(defn -main
  [& [port]]
  (let [port (Integer. (or port (env :port) 8082))]
    (run-jetty app {:port port :join? false})))

;; (def server (run-jetty jsproto.dragon.web/app {:port 8082 :join? false})))
;; (.stop server)
;; (.start server)