(ns faves-api.models.user
	(:require 
  [faves-api.lib.validator.user :as uvr]
  [faves-api.database :refer [user membership organization role location validate]]
		[crypto.password.bcrypt :as password])
	(:use (korma core)))

(defn describe []
 (select user (order :id :ASC)))

(defn find-by-id [id]
 (first (select user (where {:id id}))))

(defn find-by-username [username]
 (first (select user (where {:username username}))))

(defn find-by-email [email]
 (first (select user (where {:email email}))))

(defn find-by-access-token [token]
 (first (select user (where {:access_token token}))))

(defn find-by-username-password [u p]
 (when-let [row (first 
  (select user
   (where (or (= :email u) (= :username u)))
   (with membership 
    (with organization 
     (with location))
    (with role))))]
  (when (password/check p (:password row)) row)))

(defn create [_]
 (let [[valid obj desc] (validate user "faves-api.lib.validator.user" _)]
  (if valid
   (insert user (values obj))
   (assoc {} :errors obj :error-type desc))))