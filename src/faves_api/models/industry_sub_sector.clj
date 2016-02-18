(ns faves-api.models.industry-sub-sector
	(:require 
  [faves-api.lib.validator.industry-sub-sector :as isvr]
  [faves-api.database :refer [organization industry industry-group industry-sector industry-sub-sector validate]]
		[crypto.password.bcrypt :as password])
	(:use (korma core)))

(defn describe []
 (select industry-sub-sector 
 	(with industry-sector)
 	(with industry-group
 		(with industry)) 
 	(order :id :ASC)))

(defn find-by-id [id]
 (first (select industry-sub-sector (where {:id id}))))

(defn create [_]
 (let [[valid obj desc] (validate industry-sub-sector "faves-api.lib.validator.industry-sub-sector" _)]
  (if valid
   (insert industry-sub-sector (values obj))
   (assoc {} :errors obj :error-type desc))))