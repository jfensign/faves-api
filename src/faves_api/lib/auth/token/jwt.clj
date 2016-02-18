(ns faves-api.lib.auth.token.jwt
 (:require
  [faves-api.config 
  [jwt :refer [pk]]
  [resources :refer [private-key-dir]]]
 	[clj-jwt.core  :refer :all]
		[clj-jwt.key   :refer [private-key public-key]])
 (use clojure.java.io))

(def ^:private prv-key "dfgadfgdf")

(defn generate-token [claim]
 (-> claim jwt to-str))

(defn verify-token [claim]
 (-> claim generate-token str->jwt verify))

(defn decode-token [claim]
 (-> claim generate-token str->jwt :claims))