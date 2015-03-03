(ns jsweb.database
  (:require [clojure.java.jdbc :as sql]
            [clj-time [format :as timef] [coerce :as timec]]))

(def dbspec (or (System/getenv "DATABASE_URL")
                "postgresql://localhost:5432/jsolutions-web"))

(defn migrated? []
  (-> (sql/query dbspec 
                 [(str "select count(*) from information_schema.tables "
                       "where table_name='blog_posts'")])
      first :count pos?))

(defn migrate []
  (when (not (migrated?))
    (print "Creating database structure...") 
    (flush)
    (sql/db-do-commands dbspec
                        (sql/create-table-ddl
                         :blog_posts
                         [:id :serial "PRIMARY KEY"]
                         [:title :varchar]
                         [:summary :text "NOT NULL"]
                         [:contents :text "NOT NULL"]
                         [:tag1 "varchar(32)"]
                         [:tag2 "varchar(32)"]
                         [:tag3 "varchar(32)"]
                         [:tag4 "varchar(32)"]
                         [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]))
    (sql/db-do-commands dbspec
                        "CREATE INDEX tag1_ix ON blog_posts ( tag1 )")
    (sql/db-do-commands dbspec
                        "CREATE INDEX tag2_ix ON blog_posts ( tag2 )")
    (sql/db-do-commands dbspec
                        "CREATE INDEX tag3_ix ON blog_posts ( tag3 )")
    (sql/db-do-commands dbspec
                        "CREATE INDEX tag4_ix ON blog_posts ( tag4 )")
    (sql/db-do-commands dbspec
                        "CREATE INDEX title_ix ON blog_posts ( title )")
    (println " done")))

(defn dropdb []
  (sql/db-do-commands dbspec
                      "DROP TABLE IF EXISTS blog_posts"));

(defn get-all []
  (into [] (sql/query dbspec
                      [(str "SELECT title, summary, created_at, tag1, tag2, tag3, tag4 "
                            "FROM blog_posts "
                            "ORDER BY created_at ASC")])))

(defn get-post [title]
  (into [] (sql/query dbspec
                      [(str "SELECT * "
                            "FROM blog_posts "
                            "WHERE title = ?") 
                       title])))

(defn get-all-with-tag [tag]
  (into [] (sql/query dbspec
                      [(str "SELECT * "
                            "FROM blog_posts "
                            "WHERE tag1 = ? "
                            "OR tag2 = ? "
                            "OR tag3 = ? "
                            "OR tag4 = ?")
                       tag tag tag tag])))


(defn create-post [title summary contents created tag1 tag2 tag3 tag4]
  (sql/insert! dbspec :blog_posts
               {:title title
                :summary summary
                :contents contents
                :created_at (timec/to-timestamp 
                             (timef/parse (timef/formatter "YYYY-MM-dd HH:mm:ss") created))
                :tag1 tag1
                :tag2 tag2
                :tag3 tag3 
                :tag4 tag4}))

(defn create-post-from-list [fields]
  (create-post (nth fields 0) 
               (nth fields 6)
               (nth fields 7)
               (nth fields 1)
               (nth fields 2)
               (nth fields 3)
               (nth fields 4)
               (nth fields 5)))

(defn load-xml-posts [file-list]
  (map #(->> (clojure.xml/parse %) :content first :content 
             (map :content)
             (map first)
             (create-post-from-list))
       file-list))

; (def blog-data (clojure.xml/parse "../simplified.xml"))
; (pprint (-> blog-data :content first))
; (-> (clojure.xml/parse "doc/posts/cppObjectFactory.xml") :content first :content) -> gets the post
; (-> (clojure.xml/parse "doc/posts/cppObjectFactory.xml") :content first :content first :content) -> gets content of tag


