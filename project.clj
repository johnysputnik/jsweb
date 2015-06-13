(defproject jsolutions-web "0.1.0"
  :description "jSolutions Ltd Web Site"
  :url "http://www.jsolutions.co.uk"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/java.jdbc "0.3.2"]
				 [org.clojure/tools.nrepl "0.2.10"]
                 [ring/ring-codec "1.0.0"]
                 [ring/ring-core "1.2.1"]
                 [ring/ring-jetty-adapter "1.2.1"]
                 [ring/ring-json "0.3.1"]
                 [compojure "1.1.6"]
                 [com.cemerick/friend "0.2.1"]
                 [enlive "1.1.5"]
                 [environ "0.5.0"]
                 [org.toomuchcode/clara-rules "0.8.4"]
                 [clj-time "0.9.0"]
                 [postgresql "9.1-901.jdbc4"]
                 [me.raynes/fs "1.4.6"]]
  :min-lein-version "2.0.0"
  :main jsweb.core
  :plugins [[lein-less "1.7.2"]
            [lein-asset-minifier "0.2.2"]
            [environ/environ.lein "0.2.1"]
            [cider/cider-nrepl "0.8.2"]]
  :less {:source-paths ["src/less"]
         :target-path "target/temp/css"}
  :minify-assets {:assets {"resources/public/css/stylesheet.min.css" "target/temp/css"
                           "resources/public/js/script.js" "src/js"}}
  :uberjar-name "jsolutions-web-standalone.jar"
  :profiles {:uberjar {:aot :all}})


