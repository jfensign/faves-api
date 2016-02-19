(ns faves-api.controllers.industry-group
	(:require 
		[liberator.core :refer [defresource]]
		[faves-api.models.industry-group :as industry-group]))

(defn- handle-ok
 ^{:private true
   :doc "Returns all sectors with hierarchical children."}
 [{{{id :id} :params} :request :as _}]
 (if id
  (industry-group/find-by-id id)
  (industry-group/describe)))

(defresource base
	:allowed-methods [:get :post]
	:available-media-types ["application/json"]
	:service-available? {:representation {:media-type "application/json"}}
	:handle-ok handle-ok)