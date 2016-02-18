(defproject faves-api "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [
    [org.clojure/clojure "1.6.0"]
    [lein-ring "0.8.13" :exclusions [org.clojure/clojure]]
    [drift "1.5.3" :exclusions [org.clojure/clojure]]
    [compojure "1.3.3"]
    [ring "1.3.2"]
    [ring-basic-authentication "1.0.5"]
    [ring/ring-json "0.4.0"]
    [liberator "0.13"]
    [com.cemerick/friend "0.2.2-SNAPSHOT"]
    [clj-jwt "0.1.1"]
    [korma "0.4.0"]
    [postgresql/postgresql "8.4-702.jdbc4"]
    [com.duelinmarkers/ring-request-logging "0.2.0"]
    [ring-token-authentication "0.1.0"]
    [org.clojure/java.jdbc "0.3.6"]
    [circleci/clj-keyczar "0.1.2"]
    [com.novemberain/validateur "2.4.2"]
    [crypto-password "0.1.3"]
    [org.clojure/data.json "0.2.6"]
    [clj-http "0.9.2"]
    [slingshot "0.10.3"]
    [base64-clj "0.1.1"]
    [ring-cors "0.1.7"]
    [clj-time "0.11.0"]]
  :plugins [
    [lein-ring "0.8.13"]
    [drift "1.5.3"]]
  :ring {:handler faves-api.handler/app}
  :profiles
  {:dev {:dependencies [
          [javax.servlet/servlet-api "2.5"]
          [ring-mock "0.1.5"]]}})