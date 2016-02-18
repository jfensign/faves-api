(ns faves-api.lib.validator.industry-sector
	(:require
  [validateur.validation :refer :all]))

(def field-defs
 ^{:private false 
   :doc "Field definitions."}
 {:id   {:unique true}
  :name {:unique false}})

(def unique-fields
 ^{:private false
   :doc "Unique fields"}
 (->> field-defs (filter #(-> % second :unique)) (mapv first)))

(defn malformed?
 ^{:private false 
   :doc "Validation rules for user data."}
 [_]
 (let [v (validation-set
   (presence-of :id)
   ;(format-of :id :message "may not contain whitespace")
   (length-of :id :is 2)
   (presence-of :name))]
  (if (valid? v _) false (v _))))