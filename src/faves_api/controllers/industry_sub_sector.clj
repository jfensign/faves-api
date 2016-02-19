(ns faves-api.controllers.industry-sub-sector
	(:require 
		[liberator.core :refer [defresource]]
		[faves-api.models.industry-sub-sector :as industry-sub-sector]))

(defn- handle-ok
 ^{:private true
   :doc "Returns all sectors with hierarchical children."}
 [{{{id :id} :params} :request :as _}]
 (if id
  (industry-sub-sector/find-by-id id)
  (industry-sub-sector/describe)))

(defresource base
	:allowed-methods [:get :post]
	:available-media-types ["application/json"]
	:service-available? {:representation {:media-type "application/json"}}
	:handle-ok handle-ok)