(ns gitegylet.views
  (:require
   [re-frame.core :as rf]
   [gitegylet.subs :as subs]
   [gitegylet.repo.subs]
   [gitegylet.repo.events]))

(defn main-panel []
  (let [branches @(rf/subscribe [::subs/branches])
        statuses @(rf/subscribe [:gitegylet.repo.subs/statuses])]
    [:div {:id "main"}
     [:nav {:id "topbar"}
      [:button
       {:on-click #(rf/dispatch [:gitegylet.repo.events/show-repo-dialog])
        :title "Open repo"}
       "\uf07c"]]
     [:div {:id "flex"}
      [:ul
       (map (fn [branch] [:li {:key (gensym)} (get branch "name")]) branches)]
      (reduce
       (fn [dl status]
         (-> dl
             (conj [:dt (:file status)])
             (conj [:dd (:status status)])))
       [:dl]
       statuses)]]))
