(ns gitegylet.events
  (:require
   [re-frame.core :as rf]
   [gitegylet.db :as db]
   ))

(rf/reg-event-fx
 ::initialize-db
 (fn [_ _]
   {:db db/default-db}))