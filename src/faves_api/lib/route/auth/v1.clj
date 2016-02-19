(ns faves-api.lib.route.auth.v1
	(:require 
  [compojure.core :refer :all]
  [compojure.route :as route]
  [faves-api.controllers
  [authenticate :as authenticate]]))

(defroutes config
	(context "/authenticate" []
		(ANY "/v1" [] authenticate/authorize)))