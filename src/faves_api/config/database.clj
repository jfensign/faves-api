(ns faves-api.config.database
	(:require [clojure.string :as str]
		        [clojure.walk :as walk])
	(:import (java.net URI)))

(defn- parse-query-parts
 ^{:private true}
 [^String query ]
 (and query (for [^String kvs (.split query "&")] (vec (.split kvs "=")))))

(def subprotocols
 ^{:private true :doc "Map of schemes to subprotocols"}
 {"postgres" "postgresql"})

(defn- parse-properties-uri [^URI uri]
 (let [host (.getHost uri)
       port (if (pos? (.getPort uri)) (.getPort uri))
       path (.getPath uri)
       scheme (.getScheme uri)
       ^String query (.getQuery uri)
       query-parts (parse-query-parts query)]
    (merge
     {:subname (if host
       (if port (str "//" host ":" port path) (str "//" host path))
       (.getSchemeSpecificPart uri))
      :subprotocol (subprotocols scheme scheme)}
     (if-let [user-info (.getUserInfo uri)]
      {:user (first (str/split user-info #":"))
       :password (second (str/split user-info #":"))})
     (walk/keywordize-keys (into {} query-parts)))))

(def
	^{:private false
	  :doc "Database subprotocol. Default: \"postgresql\""}
	db-protocol
	(or (System/getenv "DB_PROTOCOL") "postgresql"))

(def
	^{:private false
	  :doc "Database port. Default: 5432"}
	db-port
	(or (System/getenv "DB_PORT") "5432"))

(def
	^{:private false
	  :doc "Database name. Default: faves"}
	db-name
	(get (System/getenv) "DB_NAME" "faves"))

(def 
	^{:private false
	  :doc "Master db connection string taken from environment variable \"DB_URL\". Default: postgresql://faves:faves@localhost:5432/faves"}
	db-uri
	(or (System/getenv "DB_URL") (str db-protocol "://faves:faves@localhost:" db-port "/" db-name)))

(println db-uri)

(def
	^{:private false
	  :doc "DB Specification for JDBC"}
	db-spec
	(parse-properties-uri (java.net.URI. db-uri)))