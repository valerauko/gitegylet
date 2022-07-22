(ns gitegylet.commits.events
  (:require [re-frame.core :as rf]
            [gitegylet.effects :as fx]
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
  ::toggle-select
  (fn [{:keys [selected-commit] :as db} [_ new-selected]]
    (if (= selected-commit new-selected)
      db
      (assoc db :selected-commit new-selected))))
