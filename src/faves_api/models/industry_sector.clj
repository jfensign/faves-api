(ns faves-api.models.industry-sector
	(:require
  [faves-api.lib.validator.industry-sector :as isvr]
  [faves-api.database :refer [organization industry-sector industry-sub-sector industry-group industry validate]]
		[crypto.password.bcrypt :as password])
	(:use (korma core)))

(defn describe []
 (select industry-sector 
 	(with industry-sub-sector
 		(with industry-group
 			(with industry)))
 	(order :id :ASC)))

(defn find-by-id [id]
 (first
  (select industry-sector
   (where {:id id})
   (with industry-sub-sector
    (with industry-group
     (with industry)))
   (order :id :ASC))))

(defn create [_]
 (let [[valid obj desc] (validate industry-sector "faves-api.lib.validator.industry-sector" _)]
  (if valid
   (insert industry-sector (values obj))
   (assoc {} :errors obj :error-type desc))))