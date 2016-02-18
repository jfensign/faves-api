(ns faves-api.controllers.industry
	(:require 
		[liberator.core :refer [defresource]]
		[faves-api.models.industry :as industry]))

(defn- handle-ok
 ^{:private true
   :doc "Returns all industries with hierarchical parents."}
 [{{{id :id} :params} :request :as _}]
 (if id
  (industry/find-by-id id)
  (industry/describe)))

(defresource base
	:allowed-methods [:get :post]
	:available-media-types ["application/json"]
	:service-available? {:representation {:media-type "application/json"}}
	:handle-ok handle-ok)