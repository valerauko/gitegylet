(ns gitegylet.branches.events
  (:require [re-frame.core :as rf]
            [gitegylet.effects :as fx]
            [gitegylet.branches.db :refer [branch->map]]))

(rf/reg-event-fx
 ::load-branches
 (fn [{:keys [db]} [_ branches]]
   {:db (assoc db :local-branches (map branch->map branches))}))

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
   (let [f (if selected? conj disj)]
     {:db (assoc db :branches-selected (f selected name))})))
      ;; :dispatch [::commits/reload]})))

(rf/reg-event-db
 ::toggle-expansion
 (fn [db [_ name expanded expanded?]]
   (let [f (if expanded? conj disj)]
     (assoc db :folders-expanded (f expanded name)))))
