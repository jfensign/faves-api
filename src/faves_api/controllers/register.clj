(ns faves-api.controllers.register
	(:require [liberator.core :refer [defresource]]
		      [faves-api.models.user  :as user]
		      [faves-api.config.codes :as codes]
		      [clojure.data.json :as json]
		      [faves-api.models.authenticate :as authenticate]))

(defn- conflict-check [_]
	(let [body (get-in _ [:request :body])
		  error-map (atom {})]
		(when-let [dups-email (user/find-by-email (:email body))]
			(swap! error-map assoc :email {:error (str "Email address " (:email body) " is taken.") 
										   :error_type :DuplicateUserEmail}))
		(when-let [dups-username (user/find-by-username (:username body))]
			(swap! error-map assoc :username {:error (str "Username " (:username body) " is taken") 
				                              :error_type :DuplicateUserUsername}))
		(when (> (count (keys @error-map)) 0) @error-map)))

;SINGUP
(defn- signup-malformed-check [_]
	(when-let [malformed (-> 
				(:profile _)
				user/malformed?)]
		{::error malformed}))

(defn- signup-handle-unprocessable [_]
	(let [validation-errors (-> 
			(:profile _)
			user/malformed?)]
		(or validation-errors 
			(conflict-check _))))

(defn- signup-perform [_]
	(when-let [user (-> 
			(:profile _)
			user/create)]
		(authenticate/create 
			(assoc (:profile _) 
					:provider (:provider _)
					:remote_addr (-> _ (get-in [:request :remote-addr]))
					:user_id (:id user)))
		{::profile user}))

(defn- signup-processable [_]
	(let [validation-errors (-> 
			(:profile _)
			user/malformed?)]
		(if validation-errors
			false
			(not (conflict-check _)))))

(defn signup-available? [_]
	(when-let [profile (authenticate/resolve-auth-fn 
				(-> _ (get-in [:request :params :provider])) 
				(-> _ (get-in [:request :query-params])))]
		{:representation {:media-type "application/json"}
   :profile (merge (-> _ (get-in [:request :body])) 
         						    profile 
         				      {:access_token (-> _ 
         				      	(get-in [:request :query-params "access_token"]))})
   :provider (-> _ (get-in [:request :params :provider]))}))


(defresource signup 
	:allowed-methods [:post]
	:service-available? signup-available?
    :available-media-types ["application/json"]
    :processable? signup-processable
    :respond-with-entity? true
    :malformed? signup-malformed-check
	:handle-unprocessable-entity signup-handle-unprocessable
	:handle-malformed ::error
	:post! signup-perform
	:handle-okay ::profile)