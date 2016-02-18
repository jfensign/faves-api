(ns faves-api.controllers.register
	(:require 
		[liberator.core :refer [defresource]]
		[faves-api.models.user  :as user]
		[faves-api.config.codes :as codes]
		[clojure.data.json :as json]
		[faves-api.models.authenticate :as authenticate]))

(defn post! [_])

(defn handle-okay [_] _)

(defn malformed? [{{body :body :as request} :request :as _}]
	(let [u (user/create body)
							{:keys [errors error-type]} u]
		(if (= :validation error-type)
			[true  {::malformed u}]
			[false (merge {} {::user u} (if error-type {::unprocessable u} {}))])))

(defn processable? [{unprocessable ::unprocessable :as _}]
	[(empty? unprocessable) _])

(defresource signup 
	:allowed-methods [:post]
	:available-media-types ["application/json"]
	:service-available? {:representation {:media-type "application/json"}}
	:respond-with-entity? true
	:malformed? malformed?
	:processable? processable?
	:handle-malformed ::malformed
	:handle-unprocessable-entity ::unprocessable
	:post! post!
	:handle-okay handle-okay)