(ns faves-api.config.provider
	(:require [clojure.string :as str]
		      [clojure.walk :as walk])
	(:import (java.net URI)))

(def ^{:private false 
	   :doc "Map of schemes to subprotocols"}
	 facebook 
	 {:auth-uri "https://graph.facebook.com/me"
	  :key-mapping {:id :provider_uuid :first_name :firstname :last_name :lastname}})

(def ^{:private false 
	   :doc "Map of schemes to subprotocols"}
	 twitter 
	 {:auth-uri "https://api.twitter.com/1.1/account/verify_credentials.json"})

(def ^{:private false 
	   :doc "Map of schemes to subprotocols"}
	 google 
	 {:auth-uri "https://www.googleapis.com/plus/v1/people/me"})

(def ^{:private false 
	   :doc "Map of schemes to subprotocols"}
	 linkedin 
	 {:auth-uri "https://api.linkedin.com/v1/people/~:(first-name,last-name,headline,picture-url,email-address,id)"})

