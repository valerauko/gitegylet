(ns gitegylet.branches.db)

(defn branch->map
  [branch]
  {:full-name (get branch "name")
   :commit-id (get branch "commitId")
   :head? (get branch "isHead")
   :ahead-behind (get branch "aheadBehind")})
