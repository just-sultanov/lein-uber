(defproject team.sultanov/lein-uber "0.0.1-SNAPSHOT"
  :description "Leiningen plugin that makes an uberjar only when source files are modified after the last build"
  :url "https://github.com/sultanov-team/lein-uber"
  :license {:name "Eclipse Public License"
            :url  "https://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :eval-in-leiningen true
  :dependencies [[babashka/fs "0.0.5"]])
