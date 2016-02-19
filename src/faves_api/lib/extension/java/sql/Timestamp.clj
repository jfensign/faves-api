(ns faves-api.lib.extension.java.sql.Timestamp
	(use clojure.data.json)
	(:gen-class))

(extend-type java.sql.Timestamp
 JSONWriter
 (-write [date out]
 (-write (str date) out)))