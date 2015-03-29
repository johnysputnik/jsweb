;; template definitions

(ns jsweb.templates
  (:require [net.cgrand.enlive-html :as html :refer [deftemplate defsnippet]]
            [clj-time [format :as timef] [coerce :as timec]]
            [clojure.string :as string]
            [ring.util.codec :as rcodec]))

;; A date formatter for displaying blog post dates
(def bd-formatter (timef/formatter "MMM d, yyyy"))

;; html enlive snippets

(defsnippet abbrev-post-snippet "home.html" 
  {[:.article-title] [[:.extract]]}
  [post]
  [:.article-title :a] (html/content (:title post))
  [:.article-title :a] (html/set-attr :href (str "/article?title=" 
                                                 (rcodec/url-encode (:title post))))
  [:.article-date] (html/content  (timef/unparse bd-formatter (timec/from-date (:created_at post))))
  [:.article-tags [:a] ] (html/clone-for [tag (filter #(not (string/blank? %))
                                               (list (:tag1 post)(:tag2 post)(:tag3 post)(:tag4 post)))]
                                         [:a] (html/content tag)
                                         [:a] (html/set-attr :href (str "/articles?tag=" 
                                                                        (rcodec/url-encode tag))))
  [:.extract] (html/content (:summary post)))

;; html enlive templates

(defmacro defbasicpage
  [name template]
  `(deftemplate ~name ~template
                [ctxt#]
                [:head :title] (html/content (:title ctxt#))))


(defbasicpage about "about.html")
(defbasicpage contact "contact.html")

;; (defbasicpage home "home.html")
(deftemplate home "home.html"
  [ctxt#]
  [:#latest-articles-list] (html/content (map abbrev-post-snippet (:data ctxt#))))

(defbasicpage articles "articles.html")

(defbasicpage article "article.html")
