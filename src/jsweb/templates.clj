;; template definitions

(ns jsweb.templates
  (:require [net.cgrand.enlive-html :as html :refer [deftemplate]]))


;; html enlive templates

(deftemplate home "main.html"
             [ctxt]
             [:head :title] (html/content (:title ctxt)))