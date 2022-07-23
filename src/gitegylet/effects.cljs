(ns gitegylet.effects
  (:require [re-frame.core :as rf]
            ["@tauri-apps/api/tauri" :refer [invoke]]))

(def interval-handler
  ;; reference https://day8.github.io/re-frame/FAQs/PollADatabaseEvery60/
  (let [live-intervals (atom {})]
    (fn handler [{:keys [action id freq event]}]
      (condp = action
        :clean (mapv #(handler {:action :end :id %})
                     (keys @live-intervals))
        :start (let [interval-id (js/setInterval #(rf/dispatch event) freq)]
                 (swap! live-intervals assoc id interval-id))
        :end   (do
                 (js/clearInterval (get @live-intervals id))
                 (swap! live-intervals dissoc id))))))

(interval-handler {:action :clean})

(re-frame.core/reg-fx
 ::interval
 interval-handler)

(rf/reg-event-fx
 ::tauri-success
 (fn [data [_ result]]
   (js/console.log "Unhandled Tauri result" result)
   data))

(rf/reg-event-fx
 ::tauri-failure
 (fn [data [_ error]]
   (js/console.error "Unhandled Tauri error" error)
   data))

(rf/reg-fx
 ::tauri
 (fn handle [args]
  (if (vector? args)
    (doseq [arg args] (handle arg))
    (let [{:keys [command args success failure]
           :or {success ::tauri-success failure ::tauri-failure}} args]
      (-> (if args (invoke command (clj->js args)) (invoke command))
          (.then #(rf/dispatch [success (js->clj %)]))
          (.catch #(rf/dispatch [failure (js->clj %)])))))))
