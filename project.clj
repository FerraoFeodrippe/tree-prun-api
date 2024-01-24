(defproject tree-prun-api "0.1.0-SNAPSHOT"
  :description "Api as a Service tor Tree Pruning Management"
  :url "https://github.com/FerraoFeodrippe/tree-prun-api"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/java.jdbc "0.7.12"]
                 [org.xerial/sqlite-jdbc "3.45.0.0"]]
  :repl-options {:init-ns tree-prun-api.core})
