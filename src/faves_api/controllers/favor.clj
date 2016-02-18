(ns faves-api.controllers.favor
	(:require [liberator.core :refer [defresource]]
		      [faves-api.models.user  :as user]
		      [faves-api.config.codes :as codes]
		      [faves-api.models.favor :as favor]
		      [clojure.data.json :as json]
		      [faves-api.models.authenticate :as authenticate]))

(defn- conflict-check [_]
	true)

(defn- favor-processable [_]
	(let [validation-errors (:profile _)]
		(if validation-errors
			false
			(not (conflict-check _)))))

(defn- favors-available? [_]
 {:representation {:media-type "application/json"}})

(defn- handle-iight [_]
	(let [user (-> (get-in _ [:request]) :user)]
		(favor/list-by-user-id (:id user))))

(defresource base
	:allowed-methods [:post :get]
	:service-available? favors-available?
    :available-media-types ["application/json"]
    :handle-ok handle-iight)