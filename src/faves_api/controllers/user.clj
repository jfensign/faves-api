(ns faves-api.controllers.user
	(:require [liberator.core :refer [defresource]]
		      [faves-api.models.user  :as user]
		      [faves-api.config.codes :as codes]
		      [clojure.data.json :as json]
		      [faves-api.models.authenticate :as authenticate]))


(defn token-auth [token]
	(user/find-by-access-token token))


(defresource select [id])

(defresource describe
	:allowed-methods [:get]
	:available-media-types ["application/json"]
	:service-available? {:representation {:media-type "application/json"}}
	:handle-ok (fn [_] (user/describe)))



