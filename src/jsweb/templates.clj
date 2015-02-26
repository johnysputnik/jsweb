;; template definitions

(ns jsweb.templates
  (:require [net.cgrand.enlive-html :as html :refer [deftemplate]]))


;; html enlive templates

(defmacro defbasicpage
  [name template]
  `(deftemplate ~name ~template
                [ctxt#]
                [:head :title] (html/content (:title ctxt#))))

(defbasicpage about "about.html")
(defbasicpage contact "contact.html")


(defbasicpage home "home.html")
(defbasicpage articles "articles.html")
(defbasicpage article "article.html")
