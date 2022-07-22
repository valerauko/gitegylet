(ns gitegylet.events
  (:require
   [re-frame.core :as rf]
   [gitegylet.db :as db]
   [gitegylet.effects :as fx]))

(rf/reg-event-fx
 ::load-branches
 (fn [{:keys [db]} [_ branches]]
   {:db (assoc db :branches branches)}))
    ;; ::fx/tauri [["commits" {:branches [branches]}]]}))

(rf/reg-event-fx
 ::initialize-db
 (fn [_ _]
   {:db db/default-db}))