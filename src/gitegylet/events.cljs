(ns gitegylet.events
  (:require
   [re-frame.core :as rf]
   [gitegylet.db :as db]
   ["@tauri-apps/api/tauri" :refer [invoke]]
   ))

(rf/reg-event-fx
 ::tauri-success
 (fn [data [_ result]]
   (js/console.log "Unhandled Tauri result" result)
   data))

(rf/reg-event-fx
 ::tauri-failure
 (fn [data [_ error]]
   (js/console.error "Unhandled Tauri error" error)
   data))

(rf/reg-fx
 :tauri
 (fn handle
   [[command success failure]]
   (-> (invoke command)
       (.then #(rf/dispatch [(or success ::tauri-success) (js->clj %)]))
       (.catch #(rf/dispatch [(or failure ::tauri-failure) (js->clj %)])))))

(rf/reg-event-db
 ::load-branches
 (fn [db [_ names]]
   (assoc db :branches names)))

(rf/reg-event-fx
 ::initialize-db
 (fn [_ _]
   {:db db/default-db
    :tauri ["branch_locals" ::load-branches]}))