(ns faves-api.lib.validator.industry
	(:require
  [validateur.validation :refer :all]))

(def field-defs
 ^{:private false
   :doc "Field definitions."}
 {:id  {:unique true}
  :name     {:unique false}
  :industry_groups_id {:unique false}})

(def unique-fields
 ^{:private false
   :doc "Unique fields"}
 (->> field-defs (filter #(-> % second :unique)) (mapv first)))

(defn malformed?
 ^{:private false 
   :doc "Validation rules for industry data."}
 [_]
 (let [v (validation-set
   (presence-of :id)
   ;(format-of :id :message "must be numeric.")
   (length-of :id :is 5)
   (presence-of :name)
   (presence-of :industry_groups_id))]
  (if (valid? v _) false (v _))))