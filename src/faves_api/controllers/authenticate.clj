(ns faves-api.controllers.authenticate
	(:require 
		[liberator.core :refer [defresource]]
		[faves-api.models.authenticate :as authenticate]
		[faves-api.models.user :as user]
		[faves-api.lib.auth.strategy.basic :as basic]))

(defn- authorized? 
	^{:private true
		 :doc "Authorize user with username/password."}
	[{{{ua "user-agent"} :headers ip :remote-addr :as req} :request un ::username pw ::password :as _}]
	(let [user (user/find-by-username-password un pw)]
		[(-> user nil? not) 
			{::user user 
				::ip ip
				::ua ua
				::unauthorized {:error "Invalid username and password combination."}}
				]))

(defn- malformed?
	^{:private true
		 :doc "Ensure authorization header is present."}
	[_]
	(let [[username password] (basic/extract-from-headers _)]
		[(not (and username password)) 
		{::username username 
			::password password 
			::malformed {:error "Requires HTTP Basic Auth."}}
			]))

(defn- processable?
	^{:private true
		 :doc "Attempts to create an authenication record."}
 [{ua ::ua ip ::ip {uid :id :as u} ::user :as _}]
 (let [auth-map (-> {} (assoc :remote_addr ip :users_id uid) authenticate/create)
 						token (:access_token auth-map)]
 	(println auth-map)
 	[(not-empty token) 
 	 {::token {:access_token token}
 	  ::unprocessable {:error "Failed to create session."}}]))

(defn- post!
 ^{:private true
 	 :doc "All good!"}
 [_]
 _)

(defresource authorize [provider]
	:allowed-methods [:post]
	:available-media-types ["application/json"]
	:service-available? {:representation {:media-type "application/json"}}
	:respond-with-entity? true
	:malformed? malformed?
	:authorized? authorized?
	:processable? processable?
	:post! post!
	:handle-malformed ::malformed
	:handle-unauthorized ::unauthorized
	:handle-unprocessable-entity ::unprocessalbe
	:handle-created ::token)