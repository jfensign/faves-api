(ns faves-api.database
	(:require [faves-api.config.database :refer [db-spec]])
	(:use (korma core db)))

(defdb master db-spec)

(declare users authentications)

(defentity users
	(pk :id)
	(table :users)
	(database master)
	(has-many authentications {:fk :user_id}))


(defentity authentications
  (pk :id)
  (table :authentications)
  (database master)
  (has-one users {:fk :user_id}))

(defentity favors
  (pk :id)
  (table :favors)
  (database master)
  (has-one users {:fk :user_id}))

(defn aggregate-count [entity opts]
	(first (select entity
  		(aggregate (count :*) :count)
  		(where (:where opts)))))

(defn paginate [entity opts]
	(let [cnt (aggregate-count entity opts)
		  res (if (= cnt 0) [] (select entity (where (:where opts))))]
		(assoc cnt :data res)))