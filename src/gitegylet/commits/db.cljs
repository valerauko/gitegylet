(ns gitegylet.commits.db)

(defn commit->map
  [{:strs [parents id author timestamp message summary column]}]
  (let [parents (js->clj parents)]
    {:id id
     :author
     (let [{:strs [name email md5]} author]
       {:name name
        :email email
        :md5 md5})
     :time timestamp
     :parents parents
     :message message
     :summary summary
     :column column}))
