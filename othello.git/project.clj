(defproject com.atomicobject/othello "0.1.0-SNAPSHOT"
  :description "Othello board to run an AI grudge match."
  :min-lein-version "2.0.0"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-midje "3.1.3"]]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.cli "0.3.1"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [ring/ring-core "1.4.0"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [http-kit "2.1.18"]
                 [cheshire "5.5.0"]
                 [prismatic/schema "0.4.3"]
                 [seesaw "1.4.5"]]
  :main ^:skip-aot othello.core
  :target-path "target/%s"
  :profiles {:dev {:dependencies [[midje "1.6.3"]]}})
