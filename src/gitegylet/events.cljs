(ns gitegylet.events
  (:require
   [re-frame.core :as rf]
   [gitegylet.db :as db]
   [gitegylet.effects :as fx]
   ["@tauri-apps/api/dialog" :as dialog]))

(rf/reg-event-fx
 ::load-branches
 (fn [{:keys [db]} [_ branches]]
   {:db (assoc db :branches branches)}))
    ;; ::fx/tauri [["commits" {:branches [branches]}]]}))

(rf/reg-event-fx
 ::load-repo
 (fn [{:keys [db]} [_ result]]
   {:db (assoc db :repo result)
    ::fx/tauri [["branch_locals"] ::load-branches]}))

(rf/reg-event-fx
 ::try-open-repo
 (fn [data [_ path]]
   (assoc data ::fx/tauri [["open_repo" {:path path}] ::load-repo])))

(rf/reg-event-fx
 ::show-repo-dialog
 (fn [data _]
   (-> (dialog/open (clj->js {:directory true}))
       (.then #(when % (rf/dispatch [::try-open-repo %]))))
   data))

(rf/reg-event-fx
 ::initialize-db
 (fn [_ _]
   {:db db/default-db}))