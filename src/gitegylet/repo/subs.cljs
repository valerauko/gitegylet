(ns gitegylet.repo.subs
  (:require
   [re-frame.core :as rf]))

(rf/reg-sub
  ::statuses
  (fn [db _]
    (:statuses db)))
