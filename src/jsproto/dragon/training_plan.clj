(ns jsproto.dragon.training-plan
  (:require [clara.rules :refer :all]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [clojure.data.json :as json]))

;; util functions

(def custom-formatter (f/formatter "dd MM yyyy"))

(defn output-training-plan
  "generate output for a training plan"
  [session]
  (println "training plan complete..."))

;; util macros

(defmacro get-start
  "gets the start date based on weeks and end date."
  [end weeks]
  `(t/minus ~end (t/weeks ~weeks)))

(defmacro get-end
  "gets the end date based on weeks and start date."
  [start weeks]
  `(t/plus ~start (t/weeks ~weeks)))

(defmacro create-training-phase [start end type]
  "Creates a training phase."
  `(do (insert! (->training-phase ~start ~end ~type))
      (println ~type "phase created from"
           (f/unparse custom-formatter ~start)
           "to"
           (f/unparse custom-formatter ~end))))

;; records

(defrecord rider [name dob level])
(defrecord race [date priority distance type])
(defrecord training-phase [start end type])

;; queries

;; query for rider
;; query for races
;; query for phases
;; query for weeks
;; query for days

;; rules


(defrule endurance-phase
         "Create an endurance training phase"
         [rider (= :basic level) (= ?name name)]
         [training-phase (= :strength type) (= ?date start)]
         =>
         (create-training-phase (get-start ?date 8)
                                 ?date
                                 :endurance))

(defrule strength-phase
         "Create a strength training phase"
         [rider (= :basic level) (= ?name name)]
         [training-phase (= :stamina type) (= ?date start)]
         =>
         (create-training-phase (get-start ?date 6)
                                 ?date
                                 :strength))

(defrule stamina-phase
         "Create a stamina training phase"
         [rider (= :basic level) (= ?name name)]
         [training-phase (= :power type) (= ?date start)]
         =>
         (create-training-phase (get-start ?date 4)
                                 ?date
                                 :stamina))

(defrule power-phase
         "Create a power training phase"
         [rider (= :basic level) (= ?name name)]
         [training-phase (= :peak type) (= ?date start)]
         =>
         (create-training-phase (get-start ?date 4)
                                 ?date
                                 :power))

(defrule rest-week
         "Creates a rest week after a peak week"
         [rider (= :basic level) (= ?name name)]
         [training-phase (= :peak type) (= ?date end)]
         =>
         (create-training-phase ?date
                                (get-end ?date 1)
                                :rest))

(defrule peak-week
         "Create a peak week."
         [rider (= :basic level) (= ?name name)]
         [race (= 1 priority) (= ?date date)]
         =>
         (create-training-phase (get-start ?date 1)
                                ?date
                                :peak))

(defn run-plan
  "Create a training plan"
  [name]
  (println "Creating a sample training plan:")
  (-> (mk-session 'jsproto.dragon.training-plan :cache false)
      (insert (->rider name (t/date-time 1971 12 20) :basic)
              (->race (t/date-time 2015 05 23) 3 40 :circuit)
              (->race (t/date-time 2015 05 30) 1 60 :hilly)
              (->race (t/date-time 2015 06 14) 2 70 :flat)
              (->race (t/date-time 2015 06 21) 2 40 :circuit)
              (->race (t/date-time 2015 06 28) 1 70 :flat)
              (->race (t/date-time 2015 05 23) 3 40 :circuit))
      (fire-rules)
      (output-training-plan)))

