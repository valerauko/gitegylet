(ns gitegylet.commits.events
  (:require [re-frame.core :as rf]
            [gitegylet.effects :as fx]
            [gitegylet.repo.db :refer [status->map]]
            [gitegylet.commits.db :refer [commit->map]]))

(rf/reg-event-db
 ::load-head
 (fn [db [_ head]]
   (assoc db :head (commit->map head))))

(rf/reg-event-fx
 ::load-commits
 (fn [{:keys [db]} [_ commits]]
   {:db (assoc db :commits (map commit->map commits))
    ::fx/tauri [["head"] ::load-head]}))

(rf/reg-event-db
 ::load-statuses
 (fn [db [_ statuses]]
   (assoc-in db
             [:diff-files (get-in db [:selected-commit :id])]
             (map status->map statuses))))

(rf/reg-event-fx
 ::toggle-select
 (fn [{{:keys [selected-commit] :as db} :db} [_ new-selected]]
   (when-not (= selected-commit new-selected)
     (let [updated {:db (assoc db :selected-commit new-selected)}
           id (:id new-selected)]
       (if (get-in db [:diff-files id])
         updated
         (assoc updated ::fx/tauri [["commit_diff" {:id id}] ::load-statuses]))))))

