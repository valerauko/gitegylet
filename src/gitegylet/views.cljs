(ns gitegylet.views
  (:require
   [re-frame.core :as rf]
   [re-com.core :as re-com :refer [at]]
   [gitegylet.config :as config]
   [gitegylet.subs :as subs]
   ))

(defn title []
  (let [name (rf/subscribe [::subs/name])]
    [re-com/title
     :src   (at)
     :label (str "Hello from " @name". Git version " config/version)
     :level :level1]))

(defn main-panel []
  [re-com/v-box
    :src      (at)
    :height   "100%"
    :children [[title]
              ]])
