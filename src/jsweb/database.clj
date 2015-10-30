(ns jsweb.database
  (:require [clojure.java.jdbc :as sql]
            [clojure.xml :as xml]
            [clj-time [format :as timef] [coerce :as timec]]
            [me.raynes.fs :as fs]))

(def dbspec (or (System/getenv "DATABASE_URL")
                "postgresql://localhost:5432/jsolutions-web"))

(defn migrated?
  "Check if the database is correct"
  []
  (-> (sql/query dbspec
                 [(str "select count(*) from information_schema.tables "
                       "where table_name='blog_posts'")])
      first
      :count
      pos?))

(defn createdb
  "Create the database tables"
  []
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

(defn dropdb
  "Drop the database tables"
  []
  (sql/db-do-commands dbspec
                      "DROP TABLE IF EXISTS blog_posts"))

(defn get-all
  "Get a summary of all posts"
  [& limit]
  (into [] (sql/query dbspec
                      [(str "SELECT id, title, summary, created_at, tag1, tag2, tag3, tag4 "
                            "FROM blog_posts "
                            "ORDER BY created_at DESC "
                            "LIMIT " (or limit "ALL"))])))

(defn get-all-with-tag 
  "Get a summary of all posts with a specific tag"
  [tag]
  (into [] (sql/query dbspec
                      [(str "SELECT id, title, summary, created_at, tag1, tag2, tag3, tag4 "
                            "FROM blog_posts "
                            "WHERE tag1 = ? "
                            "OR tag2 = ? "
                            "OR tag3 = ? "
                            "OR tag4 = ? "
                            "ORDER BY created_at DESC ")
                       tag tag tag tag])))

(defn get-post 
  [title]
  "Get the details of a specific post based on title"
  (into [] (sql/query dbspec
                      [(str "SELECT * "
                            "FROM blog_posts "
                            "WHERE title = ?") 
                       title])))


(defn create-post
  "Insert a new post into the database"
  [data]
  (sql/insert! dbspec :blog_posts
               {:title (nth data 0)
                :summary (nth data 6)
                :contents (nth data 7)
                :created_at (timec/to-timestamp 
                             (timef/parse (timef/formatter "YYYY-MM-dd HH:mm:ss") 
                                          (nth data 1)))
                :tag1 (nth data 2)
                :tag2 (nth data 3)
                :tag3 (nth data 4) 
                :tag4 (nth data 5)}))


(defn load-xml-posts
  "Take a list of XML files, and for each one, parse the file
  and extract all the tag values into a list before using that
  list to call create-post"
  [file-list]
  (print file-list)
  (map #(->> (xml/parse %)
             :content
             first
             :content
             (map :content)
             (map first)
             (create-post))
       file-list))

(defn reload-posts
  "Recreate db and load posts from a local folder"
  [dir]
  (do
    (dropdb)
    (createdb)
    (load-xml-posts (fs/find-files dir #"post_.*\.xml"))))
