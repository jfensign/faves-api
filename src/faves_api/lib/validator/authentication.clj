(ns faves-api.lib.validator.authentication
 (:require 
  [validateur.validation :refer :all]))

(def field-defs
 ^{:private true
   :doc "Field definitions."}
 {:access_token  {:unique true}
  :provider     {:unique false}
  :provider_uuid {:unique false}
  :remote_addr  {:unique false}
  :users_id  {:unique false}})

(def unique-fields
 ^{:private false}
 (->> field-defs (filter #(-> % second :unique)) (mapv first)))

(defn malformed?
 ^{:private false 
   :doc "Validation rules for authentication data."}
 [authentication]
 (let [v (validation-set
   (presence-of :users_id)
   (numericality-of :users_id :only-integer true :message :gte 0 "users_id must be an integer."))]
  (if (valid? v authentication) false (v authentication))))