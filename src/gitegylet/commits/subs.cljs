(ns gitegylet.commits.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
  ::commits
  (fn [{:keys [commits]}]
    commits))

(rf/reg-sub
  ::head
  (fn [{:keys [head]}]
    head))

(rf/reg-sub
 ::selected
 (fn [{:keys [selected-commit]}]
   selected-commit))