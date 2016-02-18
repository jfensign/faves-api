(ns faves-api.database
	(:require 
  [faves-api.config.database :refer [db-spec]]
  [faves-api.lib.auth.token.jwt :as jwt]
  [clj-time.core :refer [now plus days]]
  [crypto.password.bcrypt :as password])
	(:use (korma core db)))

(defdb master db-spec)

;;Core relations
(declare user membership role authentication organization relationship)
;;geo/postal relations
(declare location street_name street_number street_suffix street_type direction address_unit_identifier local_municipality city district postal_code country continent)
;;type/classification relations
(declare industry industry-group industry-sub-sector industry-sector entity-type relationship-type)

(defentity continent
 (pk :id)
 (table :continents)
 (has-many country)
 (database master))

(defentity country
 (pk :id)
 (table :countries)
 (belongs-to continent)
 (database master))

(defentity city
 (pk :id)
 (table :cities)
 (database master))

(defentity postal_code
 (pk :id)
 (table :postal_codes)
 (database master))

(defentity district
 (pk :id)
 (table :districts)
 (database master))

(defentity relationship-type
 (pk :id)
 (table :relationship_types)
 (has-many relationship)
 (database master))

(defentity entity-type
 (pk :id)
 (table :entity_types)
 (database master))

(defentity industry-sector
 (pk :id)
 (table :industry_sectors)
 (database master)
 (has-many industry-sub-sector))

(defentity industry-sub-sector
 (pk :id)
 (table :industry_sub_sectors)
 (database master)
 (belongs-to industry-sector)
 (has-many industry-group))

(defentity industry-group
 (pk :id)
 (table :industry_groups)
 (database master)
 (belongs-to industry-sub-sector)
 (has-many industry))

(defentity industry
 (pk :id)
 (table :industries)
 (database master)
 (has-many organization)
 (belongs-to industry-group))

(defentity role
 (pk :id)
 (table :roles)
 (database master)
 (has-many user {:fk :role_id}))

(defentity user
	(pk :id)
	(table :users)
	(database master)
 (belongs-to role)
	(has-many authentication)
 (has-many membership)
 (prepare (fn [{pw :password :as v}]
  (assoc v :password (password/encrypt pw)))))

(defentity authentication
  (pk :id)
  (table :authentications)
  (database master)
  (prepare (fn [v]
   (let [claim {:iss (:users_id v)
                :exp (plus (now) (days 1))
                :iat (now)}]
    (assoc v :access_token (jwt/generate-token claim))))))

(defentity membership
 (pk :id)
 (table :memberships)
 (database master)
 (belongs-to organization)
 (belongs-to role))

(defentity organization
  (pk :id)
  (table :organizations)
  (database master)
  (has-many membership)
  (has-many location))

(defentity location
 (pk :id)
 (table :locations)
 (database master)
 (belongs-to organization))

(defentity relationship
 (pk :id)
 (table :relationships)
 (database master)
 (belongs-to relationship-type)
 (belongs-to entity-type)
 (belongs-to user))

(defentity favor relationship)

(defn- ns-res 
 ([^String f]
  (-> f symbol resolve))
 ([^String n ^String f]
  (->> f symbol (ns-resolve (symbol n)))))

(defn- get-validator-unique-fields [^String n]
 (->> "unique-fields" (ns-res n) var-get))

(defn- get-validator-field-defs [^String n]
 (->> "field-defs" (ns-res n) var-get))

(defn- get-validator-malformed? [^String n]
 (ns-res n "malformed?"))

(defn- get-relation [^String r]
 (ns-res r))

(defn aggregate-count [e opts]
	(first (select e
  		(aggregate (count :*) :count)
  		(where (:where opts)))))

(defn paginate [e opts]
	(let [cnt (aggregate-count e opts)
		     res (if (= cnt 0) [] (select e (where (:where opts))))]
		(assoc cnt :data res)))

(defn- ensure-uniq [relation validator _]
 (let [unique-fields (get-validator-unique-fields validator)
       unique-vals (select-keys _ unique-fields)
       dup-records (select relation (where (apply or (mapv #(conj {} %) unique-vals))))
       dups (conj (map #(select-keys % unique-fields) dup-records) _)]
  (when (-> dups count (> 1))
   (filter #(-> % (group-by dups) vals count (= 1)) unique-fields))))

(defn- ensure-valid [relation validator _]
 (let [sanitized (select-keys _ (keys (get-validator-field-defs validator)))]
  [((get-validator-malformed? validator) sanitized) sanitized]))

(defn validate [relation validator _]
 (let [dups (ensure-uniq relation validator _)]
  (if (not-empty dups)
   [false dups :dups]
   (let [[validation-errors sanitized] (ensure-valid relation validator _)]
    (if-not validation-errors
     [true sanitized]
     [false validation-errors :validation])))))