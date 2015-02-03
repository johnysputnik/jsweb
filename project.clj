(defproject jsolutions-web "0.1.0"
  :description "jSolutions Ltd Web Site"
  :url "http://www.jsolutions.co.uk"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring/ring-core "1.2.1"]
                 [ring/ring-jetty-adapter "1.2.1"]
                 [ring/ring-json "0.3.1"]
                 [compojure "1.1.6"]
                 [com.cemerick/friend "0.2.1"]
                 [enlive "1.1.5"]
                 [environ "0.5.0"]]
  :min-lein-version "2.0.0"
  :plugins [[lein-less "1.7.2"]
            [lein-asset-minifier "0.2.2"]
            [environ/environ.lein "0.2.1"]]
  :less {:source-paths ["src/less"]
         :target-path "target/temp/css"}
  :minify-assets {:assets {"target/public/css/stylesheet.min.css" "target/temp/css"
                           "target/public/js/script.js" "src/js"}}
  :uberjar-name "jsolutions-web-standalone.jar"
  :profiles {:production {:env {:production true}}})

; lein less once
; lein minify-assets
; lein compile

; :hooks [leiningen.less]
; :hooks [minify-assets.plugin/hooks]

