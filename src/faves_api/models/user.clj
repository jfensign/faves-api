(ns faves-api.models.user
	(:require [faves-api.database :refer [users]]
		      [validateur.validation :refer :all]
		      [crypto.password.pbkdf2 :as password])
	(:use (korma core)))

(def email-format #"[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")

(def username-format #"^[^\s]*$")

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
	       :doc "Validation rules for user data."}
	     malformed? [user]
       	(let [v (validation-set
  		 (presence-of :username)
         (format-of :username
                    :format username-format
                   	:message "may not contain whitespace")
         (presence-of :email)
         (format-of :email
                    :format email-format
                    :message "Valid email address is required.")
  							(presence-of :firstname)
  							(presence-of :lastname))]
    					(if (valid? v user)
    	  			false
    	  			(v user))))

(defn ^{:private false
	       :doc "Validation for authentications"}
	     malformed-signin? [credentials]
	     	(let [v (validation-set
	     		(presence-of :username)
	     		(presence-of :password))]
	     	(if (valid? v credentials)
	     		false
	     		(v credentials))))

(defn describe []
	(select users
		(order :id :ASC)))

(defn find-by-id [id]
	(first (select users (where {:id id}))))

(defn find-by-username [username]
	(first (select users (where {:username username}))))

(defn find-by-email [email]
	(first (select users (where {:email email}))))

(defn find-by-access-token [token]
	(first (select users (where {:access_token token}))))

(defn create [user]
	(let [sanitized (select-keys user (keys editable-fields))
		  validation-errors (malformed? sanitized)]
		  (println sanitized)
		(if-not validation-errors
		        (insert users (values sanitized))
		        validation-errors)))

(defn authenticate [credentials]
	(let [validation-errors (malformed-signin? credentials)
		     {:keys [password username]} credentials]
		(if-not validation-errors
			(first (select users
				(and {:password (password/encrypt password)} 
					    (or {:username username} 
					    	   {:email username})))))))

