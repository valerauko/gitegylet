(ns gitegylet.repo.events
  (:require [re-frame.core :as rf]
            [gitegylet.repo.db :refer [status->map]]
            [gitegylet.effects :as fx]
            ["@tauri-apps/api/dialog" :as dialog]
            [gitegylet.branches.events :as branch]))

(rf/reg-event-db
 ::update-statuses
 (fn [db [_ statuses]]
   (assoc db :statuses (map status->map statuses))))

(rf/reg-event-fx 
 ::poll-status
 (fn [{:keys [db]} _]
   {:db db
    ::fx/tauri {:command "statuses"
                :success ::update-statuses}}))

(rf/reg-event-fx
 ::load-repo
 ;; discards current app-db on successful repo open
 (fn [_ [_ result]]
   {:db {:repo result}
    ::fx/tauri [{:command "branch_locals"
                 :success ::branch/load-branches}
                {:command "statuses"
                 :success ::update-statuses}]
    ::fx/interval {:action :start
                   :id :status-poll
                   :freq 2000
                   :event [::poll-status]}}))

(rf/reg-event-fx
 ::try-open-repo
 (fn [{:keys [db]} [_ path]]
   {:db db
    ::fx/tauri {:command "open_repo"
                :args {:path path}
                :success ::load-repo}}))

(rf/reg-event-fx
 ::show-repo-dialog
 (fn [{:keys [db]} _]
   (-> (dialog/open (clj->js {:directory true}))
       (.then #(when % (rf/dispatch [::try-open-repo %]))))
   {:db db}))