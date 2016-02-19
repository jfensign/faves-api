(ns faves-api.models.industry-group
	(:require 
  [faves-api.lib.validator.industry-group :as isvr]
  [faves-api.database :refer [organization industry-sector industry-group industry-sub-sector industry validate]]
		[crypto.password.bcrypt :as password])
	(:use (korma core)))

(defn describe []
 (select industry-group 
 	(with industry-sub-sector
 		(with industry-sector))
 	(with industry)
 	(order :id :ASC)))

(defn find-by-id [id]
 (select industry-group
 	(where {:id id}) 
 	(with industry-sub-sector
 		(with industry-sector))
 	(with industry)
 	(order :id :ASC)))

(defn create [_]
 (let [[valid obj desc] (validate industry-group "faves-api.lib.validator.industry-group" _)]
  (if valid
   (insert industry-group (values obj))
   (assoc {} :errors obj :error-type desc))))