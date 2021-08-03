(ns leiningen.uber
  (:require
    [babashka.fs :as fs]
    [clojure.set :as set]
    [clojure.string :as str]
    [leiningen.core.main :as lein.main]
    [leiningen.core.project :as lein.project]
    [leiningen.jar :as lein.jar]))


(defn get-source-paths
  "Returns a collection of paths to the source files."
  [{:keys [source-paths java-source-paths resource-paths]}]
  (concat source-paths java-source-paths resource-paths))


(defn run-task
  "Run leiningen task."
  [project task args]
  (binding [lein.main/*exit-process?* false]
    (lein.main/resolve-and-apply project (cons task args))))


(defn uber
  "Makes an uberjar only when source files are modified after the last build."
  [project & args]
  (let [scoped-profiles   (set (lein.project/pom-scope-profiles project :provided))
        default-profiles  (set (lein.project/expand-profile project :default))
        provided-profiles (remove
                            (set/difference default-profiles scoped-profiles)
                            (-> project meta :included-profiles))
        project           (->> provided-profiles
                            (into [:uberjar])
                            (lein.project/merge-profiles project))
        project           (update-in project [:jar-inclusions] concat (:uberjar-inclusions project))
        filename          (lein.jar/get-jar-filename project :standalone)
        source-paths      (get-source-paths project)
        changes           (fs/modified-since filename source-paths)]
    (if-not (seq changes)
      (lein.main/info (format "No changes were found. The compiled uberjar is up-to-date - %s" filename))
      (do
        (lein.main/debug
          (->> changes
            (map (partial fs/relativize (:root project)))
            (cons (format "%s changes were found:" (count changes)))
            (str/join (System/lineSeparator))))
        (run-task project "uberjar" args)))))
