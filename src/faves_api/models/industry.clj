(ns faves-api.models.industry
	(:require 
  [faves-api.lib.validator.industry :as isvr]
  [faves-api.database :refer [organization industry-sector industry-sub-sector industry-group industry validate]]
		[crypto.password.bcrypt :as password])
	(:use (korma core)))

(defn describe []
 (select industry 
 	(with industry-group
 		(with industry-sub-sector
 			(with industry-sector)))
 	(order :id :ASC)))

(defn find-by-id [id]
 (first
 	(select industry 
 		(where {:id id})
 		(with industry-group
 			(with industry-sub-sector
 				(with industry-sector)))
 		(order :id :ASC))))

(defn create [_]
 (let [[valid obj desc] (validate industry "faves-api.lib.validator.industry" _)]
  (if valid
   (insert industry (values obj))
   (assoc {} :errors obj :error-type desc))))