(ns faves-api.lib.route.static.v1
	(:require
  [compojure.core :refer :all]
  [compojure.route :as route]
  [faves-api.controllers 
	[static :as static]]))

(defroutes config
 (GET "/" []
  static/index-page)
 (GET "/sign-in" []
  static/sign-in-page)
 (GET "/sign-up" []
  static/sign-up-page)
 (GET "/home" []
  static/home))