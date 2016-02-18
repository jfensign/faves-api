(ns faves-api.models.favor
	(:require [faves-api.database :refer [favor paginate]]
		      [validateur.validation :refer :all]
		      [crypto.password.pbkdf2 :as password])
	(:use (korma core)))

(def ^{:private true
	      :doc "Field definitions."}
	 editable-fields
	    {:username  {:unique true}
	     :email     {:unique true}
	     :firstname {:unique false}
	     :lastname  {:unique false}
	     :password  {:unique false}
	     :birthdate {:unique false}
	     :access_token {:unique true}})

(defn ^{:private false
	    :doc "Validation rules for favor data."}
	     malformed? [f]
       	(let [v (validation-set
  		 (presence-of :title)
         (presence-of :instructions))]
    	 
    	 (if (valid? v f)
    	  	 false
    	  	 (v f))))

(defn list-by-user-id [id]
	(paginate favor {:where {:user_id id}}))

(defn find-by-id [id]
	(first 
		(select favor 
			(where {:id id}))))

(defn create [favor]
	(let [sanitized (select-keys favor (keys editable-fields))
		  validation-errors (malformed? sanitized)]
		(println sanitized)
		(if-not validation-errors
		        (insert favor (values sanitized))
		        validation-errors)))