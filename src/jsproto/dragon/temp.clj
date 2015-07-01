(ns jsproto.dragon.temp
  (:refer-clojure :exclude [==])
  (:require [clara.rules.accumulators :as acc]
            [clara.rules :refer :all]))

;; util functions

;; (def custom-formatter (f/formatter "dd MM yyyy"))



;;;; Facts used in the examples below.

(defrecord Order [year month day])

(defrecord Customer [status])

(defrecord Purchase [cost item])

(defrecord Discount [reason percent])

(defrecord Total [total])

;;;; Some example expertsystem. ;;;;

(defrule total-purchases
         (?total <- (acc/sum :cost) :from [Purchase])
         =>
         (insert! (->Total ?total)))

;;; Discounts.
(defrule summer-special
         "Place an order in the summer and get 20% off!"
         [Order (#{:june :july :august} month)]
         =>
         (insert! (->Discount :summer-special 20)))

(defrule vip-discount
         "VIPs get a discount on purchases over $100. Cannot be combined with any other discount."
         [Customer (= status :vip)]
         [Total (> total 100)]
         =>
         (insert! (->Discount :vip 10)))

(def max-discount
  "Accumulator that returns the highest percentage discount."
  (acc/max :percent :returns-fact true))

(defquery get-best-discount
          "Query to find the best discount that can be applied"
          []
          [?discount <- max-discount :from [Discount]])


(defn run-discount
  "Function to run the above example."
  []
  (let [session (-> (mk-session 'jsproto.dragon.temp :cache true) ; Load the expertsystem.
                    (insert (->Customer :vip)
                            (->Order 2013 :march 20)
                            (->Purchase 20 :gizmo)
                            (->Purchase 120 :widget)) ; Insert some facts.
                    (fire-rules))]
    (println-str (query session get-best-discount))))