(ns jsproto.dragon.engine
  (:refer-clojure :exclude [==])
  (:require [clara.rules.accumulators :as acc]
            [clara.rules :refer :all]))

;; User entered information

;;(defrecord Event [type date distance priority terrain target result])
;;(defrecord Rider [name weight height gender age level distance_day rest_day group_day])
;;(defrecord Goal [year type date measure])

;; Training Plan information generated from the user entered information

;;(defrecord Plan [year type])
;;(defrecord Phase [year type start end])
;;(defrecord Week [year number focus])
;;(defrecord Day [year week day type distance time description])

;; Diary entries can be used to adjust the weekly and phase plan

;;(defrecord Diary [year])
;;(defrecord DiaryEntry [year week day resting completed fitness notes])

(defrecord Rider [name level])
(defrecord Plan [level])
(defrecord TrainingPhase [type])

(defrule create-plan
         [Rider (= level :basic)]
         =>
         (insert! (->Plan :basic)))

(defrule create-phase
         [Plan (= level :basic)]
         =>
         (do
           (insert! (->TrainingPhase :base))
           (insert! (->TrainingPhase :strength))
           (insert! (->TrainingPhase :speed))
           (insert! (->TrainingPhase :rest))))

(defquery get-plan
          []
          [?phase <- TrainingPhase])

(defn run-rules
  "Function to run the above example."
  []
  (let [session (-> (mk-session 'jsproto.dragon.engine :cache false)
                    (insert (->Rider "John Cumming " :basic))
                    (fire-rules))]
    (query session get-plan)))