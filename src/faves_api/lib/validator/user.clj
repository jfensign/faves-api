(ns faves-api.lib.validator.user
	(:require 
  [validateur.validation :refer :all]))

(def email-format #"[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")

(def username-format #"^[^\s]*$")

(def field-defs
 ^{:private false 
   :doc "Field definitions."}
 {:username  {:unique true}
  :email     {:unique true}
  :firstname {:unique false}
  :lastname  {:unique false}
  :password  {:unique false}
  :birthdate {:unique false}})

(def unique-fields
 ^{:private false}
 (->> field-defs (filter #(-> % second :unique)) (mapv first)))

(defn malformed?
 ^{:private false 
   :doc "Validation rules for user data."}
 [_]
 (let [v (validation-set
   (presence-of :username)
   (format-of :username :format username-format :message "may not contain whitespace")
   (presence-of :email)
   (format-of :email :format email-format :message "Valid email address is required.")
   (presence-of :password)
   (length-of :password :within (range 5 15) :message "Password must be 5-15 characters.")
   (presence-of :firstname)
   (presence-of :lastname))]
  (if (valid? v _) false (v _))))