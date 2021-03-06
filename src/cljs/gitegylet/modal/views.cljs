(ns gitegylet.modal.views
  (:require [re-frame.core :as rf]
            [gitegylet.modal.events :as events]
            [gitegylet.modal.subs :as subs]))

(defn modal
  []
  (let [content (or @(rf/subscribe [::subs/modal]) [:span])]
    [:dialog
     {:on-click #(.close (.-target %))
      :on-cancel #(rf/dispatch [::events/reset])}
     [:div
      {:on-click #(.stopPropagation %)}
      content]]))
