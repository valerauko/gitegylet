(ns gitegylet.branches.subs
  (:require [re-frame.core :as rf]
            [clojure.string :refer [join split]]))

(rf/reg-sub
 ::locals
 (fn [{:keys [local-branches]} _]
   local-branches))

(rf/reg-sub
 ::names
 :<- [::locals]
 (fn [branches]
   (map :full-name branches)))

;; selection

(rf/reg-sub
 ::-selected
 (fn [db _]
   (:branches-selected db)))

(rf/reg-sub
 ; use the branches-selected key in the db if present else select every branch
 ::names-selected
 :<- [::names]
 :<- [::-selected]
 (fn [[all-names selected] _]
   (or selected (into #{} all-names))))

;; tree expansion

(rf/reg-sub
 ::-folders-expanded
 (fn [db _]
   (:folders-expanded db)))

(rf/reg-sub
 ; use the folders-expanded key in the db if present else expand every folder
 ::folders-expanded
 :<- [::names]
 :<- [::-folders-expanded]
 (fn [[all-names expanded] _]
   (if expanded
     expanded
     (into #{}
           (comp (map #(-> % (split #"/") (butlast)))
                 (filter (complement empty?))
                 (map #(join "/" %)))
           all-names))))
