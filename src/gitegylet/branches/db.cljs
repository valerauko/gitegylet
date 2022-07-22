(ns gitegylet.branches.db)

(defn branch->map
  [branch]
  {:full-name (get branch "name")
   :commit-id (get branch "commit")
   :ref (get branch "refname")
   :head? (get branch "is_head")
   :ahead-behind (get branch "ahead_behind")})
