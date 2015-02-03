;; server definition

(ns jsweb.core
  (:require [jsweb.routes :refer [app]]
      [compojure.handler :refer [site]]
            [ring.adapter.jetty :refer [run-jetty]]
            [environ.core :refer [env]]))



;; The main function to run the server

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (run-jetty (site #'app) {:port port :join? false})))
