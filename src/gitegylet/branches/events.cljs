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
      ::fx/tauri [{:command "commits"
                   :args {:branches names}
                   :success ::commits/load-commits}
                  {:command "head"
                   :success ::commits/load-head}]})))

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

(rf/reg-event-fx
 ::checkout-success
 (fn [_ _]
   {::fx/tauri {:command "branch_locals"
                :success ::load-branches}}))

(rf/reg-event-fx
 ::checkout
 (fn [_ [_ name]]
   {::fx/tauri {:command "checkout_branch"
                :args {:name name}
                :success ::checkout-success}}))

(rf/reg-event-fx
 ::toggle-selection
 (fn [{:keys [db]} [_ name selected selected?]]
   (let [f (if selected? conj disj)
         names (f selected name)]
     {:db (assoc db :branches-selected names)
      ::fx/tauri {:command "commits"
                  :args {:branches names}
                  :success ::commits/load-commits}})))

(rf/reg-event-db
 ::toggle-expansion
 (fn [db [_ name expanded expanded?]]
   (let [f (if expanded? conj disj)]
     (assoc db :folders-expanded (f expanded name)))))
