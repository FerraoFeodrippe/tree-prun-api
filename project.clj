(defproject tree-prun-api "0.1.0-SNAPSHOT"
  :description "Api as a Service tor Tree Pruning Management"
  :url "https://github.com/FerraoFeodrippe/tree-prun-api"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [com.github.seancorfield/next.jdbc "1.3.909"]
                 [org.xerial/sqlite-jdbc "3.45.0.0"]
                 [ring/ring-core "1.11.0"]
                 [ring/ring-jetty-adapter "1.11.0"]
                 [ring/ring-defaults "0.4.0"]
                 [compojure "1.7.0"]
                 [org.clojure/data.json "2.5.0"]
                 [clj-http "3.12.3"]]
  :repl-options {:init-ns tree-prun-api.core}
  :profiles
  {:dev {:dependencies [[ring/ring-mock "0.4.0"]]
         :jvm-opts ["-DENV=dev"]}
   :prod {:jvm-opts ["-DENV=prod"]}})
  