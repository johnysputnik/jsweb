;; server definition

(ns jsweb.core
  (:require [jsweb.routes :refer [app]]
            [compojure.handler :refer [site]]
            [ring.adapter.jetty :refer [run-jetty]]
            [environ.core :refer [env]]
            [jsweb.database :as data]
			[clojure.tools.nrepl.server :only (start-server stop-server)])
	(:gen-class))



;; The main function to run the server

(defn -main [& [port]]
  (data/createdb)
  (let [port (Integer. (or port (env :port) 8080))]
    (run-jetty (site #'app) {:port port :join? false})
	(defonce server (clojure.tools.nrepl.server/start-server :port 7888))))
