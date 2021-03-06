(ns gitegylet.modal.events
  (:require [re-frame.core :as rf]))

(rf/reg-event-fx
  ::popup
  (fn [_]
    (-> "dialog"
        (js/document.querySelector)
        (.showModal))))

(rf/reg-event-db
  ::reset
  (fn [{:keys [modal] :as db}]
    (assoc db :modal nil)))

(rf/reg-event-fx
  ::show
  (fn [{:keys [db]} [_ content]]
    {:db (assoc db :modal content)
     :dispatch [::popup]}))
