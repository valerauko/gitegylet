(ns gitegylet.repo.db)

(defn status->map
  [item]
  {:file (get item "file")
   :status (get item "status")})
