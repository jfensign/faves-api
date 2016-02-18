(ns faves-api.models.authenticate
 (:require
  [faves-api.lib.validator.authentication :as avr]
  [faves-api.database :refer [authentication validate]])
 (:use (korma core)))

(defn create
 ^{:public true
   :doc "Create a new authentication record."}
 [_]
 (let [[valid obj desc] (validate authentication "faves-api.lib.validator.authentication" _)]
  (if valid
   (insert authentication (values obj))
   (assoc {} :errors obj :error-type desc))))

