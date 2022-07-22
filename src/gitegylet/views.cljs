(ns gitegylet.views
  (:require
   [re-frame.core :as rf]
   [gitegylet.branches.views :refer [branches]]
   [gitegylet.commits.views :refer [commits selected-commit-pane]]
   [gitegylet.repo.subs]
   [gitegylet.repo.events]))

(defn main-panel []
  [:div {:id "main"}
   [:nav {:id "topbar"}
    [:button
     {:on-click #(rf/dispatch [:gitegylet.repo.events/show-repo-dialog])
      :title "Open repo"}
     "\uf07c"]]
   [:div {:id "flex"}
    (branches)
    (commits)
    (selected-commit-pane)]])
