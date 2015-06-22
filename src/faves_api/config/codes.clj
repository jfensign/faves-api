(ns faves-api.config.codes
	(:require [clojure.string :as str]
		      [clojure.walk :as walk])
	(:import (java.net URI)))

(def ^{:private false 
	   :doc "Map of schemes to subprotocols"}
	 not-registered 
	 {:code 10
	  :reason "User not registered."})