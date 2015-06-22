(ns faves-api.controllers.authenticate
	(:require [liberator.core :refer [defresource]]
		      [faves-api.models.user  :as user]
		      [faves-api.config.codes :as codes]
		      [clojure.data.json :as json]
		      [faves-api.models.authenticate :as authenticate]))

(defn auth-available? [_]
	(when-let [profile (authenticate/resolve-auth-fn 
				(-> _ (get-in [:request :params :provider])) 
				(-> _ (get-in [:request :query-params])))]
		{:representation {:media-type "application/json"}
         :profile profile
         :provider (-> _ (get-in [:request :params :provider]))}))

(defn- authorize-authorized [_]
	(let [account (authenticate/find-by-provider-user-id
					(-> _ (get-in [:request :params :provider]))
					(-> _ (get-in [:request :query-params])))]
		(when account 
			  {::profile account})))

(defn- handle-not-registered [_]
	{:error codes/not-registered})

(defresource authorize [provider]
	:allowed-methods [:get]
	:service-available? auth-available?
	:available-media-types ["application/json"]
	:authorized? authorize-authorized
	:handle-unauthorized handle-not-registered
	:handle-ok ::profile)