(ns gitegylet.branches.events
  (:require [re-frame.core :as rf]
            [gitegylet.effects :as fx]
            [gitegylet.commits.events :as commits]
            [gitegylet.branches.db :refer [branch->map]]))

(rf/reg-event-fx
 ::load-branches
 (fn [{:keys [db]} [_ branches]]
   (let [converted (map branch->map branches)
         names (map :full-name converted)]
     {:db (assoc db :local-branches converted)
      ::fx/tauri [["commits" {:branches names}] ::commits/load-commits]})))

;; (rf/reg-event-fx
;;  ::create
;;  (fn [{{:keys [repo] :as db} :db} [_ commit-id name]]
;;    (.createBranch git repo commit-id name)
;;    {:dispatch [::reload]}))

;; (rf/reg-event-fx
;;  ::fetch
;;  (fn [{{:keys [repo] :as db} :db} [_ names]]
;;    (let [branches (map branch->map (.fetch git repo (clj->js names)))]
;;      {:dispatch [::reload]})))

;; (rf/reg-event-fx
;;  ::checkout
;;  (fn [{{:keys [repo]} :db} [_ name]]
;;    (.checkoutBranch git repo name)
;;    {::fx/tauri [["branch_locals"] ::load-branches]
;;     :dispatch [::commits/reload-head]}))

(rf/reg-event-fx
 ::toggle-selection
 (fn [{:keys [db]} [_ name selected selected?]]
   (let [f (if selected? conj disj)
         names (f selected name)]
     {:db (assoc db :branches-selected names)
      ::fx/tauri [["commits" {:branches names}] ::commits/load-commits]})))

(rf/reg-event-db
 ::toggle-expansion
 (fn [db [_ name expanded expanded?]]
   (let [f (if expanded? conj disj)]
     (assoc db :folders-expanded (f expanded name)))))
