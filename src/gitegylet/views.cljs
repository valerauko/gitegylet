(ns gitegylet.views
  (:require
   [re-frame.core :as rf]
   [gitegylet.events :as events]
   [gitegylet.subs :as subs]))

(defn main-panel []
  (let [branches @(rf/subscribe [::subs/branches])]
    [:div {:id "main"}
     [:nav {:id "topbar"}
      [:button
       {:on-click #(rf/dispatch [::events/show-repo-dialog])
        :title "Open repo"}
       "\uf07c"]]
     [:div {:id "flex"}
      [:ul
       (map (fn [branch] [:li {:key (gensym)} (get branch "name")]) branches)]]]))
