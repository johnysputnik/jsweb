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
                                                 (rcodec/form-encode (:title post))))
  [:.article-date] (html/content  (timef/unparse bd-formatter (timec/from-date (:created_at post))))
  [:.article-tags [:a] ] (html/clone-for [tag (filter #(not (string/blank? %))
                                               (list (:tag1 post)(:tag2 post)(:tag3 post)(:tag4 post)))]
                                         [:a] (html/content tag)
                                         [:a] (html/set-attr :href (str "/articles?tag=" 
                                                                        (rcodec/form-encode tag))))
  [:.extract] (html/content (:summary post)))

(defsnippet full-post-snippet "article.html" 
  {[:.article-title] [[:#icons-bottom]]}
  [post]
  [:.article-title] (html/content (:title post))
  [:.article-date] (html/content  (timef/unparse bd-formatter (timec/from-date (:created_at post))))
  [:.article-tags [:a] ] (html/clone-for [tag (filter #(not (string/blank? %))
                                               (list (:tag1 post)(:tag2 post)(:tag3 post)(:tag4 post)))]
                                         [:a] (html/content tag)
                                         [:a] (html/set-attr :href (str "/articles?tag=" 
                                                                        (rcodec/form-encode tag))))
            [:.post-to-twitter] (html/set-attr :href (str "http://twitter.com/home/?status="
                                                          (:title post)
                                                          ". via @jsolutionsuk"))
            [:.post-to-facebook] (html/set-attr :href (str "http://www.facebook.com/share.php?u="
                                                           "http://www.jsolutions.co.uk/article?title="
                                                           (rcodec/form-encode (:title post))))
  [:.extract] (html/content (:summary post))
  [:.article-content] (html/html-content (:contents post)))

;; html enlive templates

(defmacro defbasicpage
  [name template]
  `(deftemplate ~name ~template
                [ctxt#]
                [:head :title] (html/content (:title ctxt#))))


(defbasicpage about "about.html")
(defbasicpage contact "contact.html")

(deftemplate home "home.html"
  [ctxt#]
  [:#latest-articles-list] (html/content (map abbrev-post-snippet (:data ctxt#))))

(deftemplate articles "articles.html"
  [ctxt#]
  [:#latest-articles-list] (html/content (map abbrev-post-snippet (:data ctxt#))))

(deftemplate article "article.html"
  [ctxt#]
  [:#article-detail] (html/content (map full-post-snippet (:data ctxt#)))
  [:#article-detail] (html/set-attr :title (:title (first (:data ctxt#)))))
