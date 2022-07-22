(ns gitegylet.views
  (:require
   [re-frame.core :as rf]
   [gitegylet.branches.views :refer [branches]]
   [gitegylet.repo.subs]
   [gitegylet.repo.events]))

(defn main-panel []
  (let [statuses @(rf/subscribe [:gitegylet.repo.subs/statuses])]
    [:div {:id "main"}
     [:nav {:id "topbar"}
      [:button
       {:on-click #(rf/dispatch [:gitegylet.repo.events/show-repo-dialog])
        :title "Open repo"}
       "\uf07c"]]
     [:div {:id "flex"}
      (branches)
      (reduce
       (fn [dl status]
         (-> dl
             (conj [:dt (:file status)])
             (conj [:dd (:status status)])))
       [:dl]
       statuses)]]))
