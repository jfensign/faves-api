(ns faves-api.controllers.static
	(:require 
  [faves-api.config.resources :as r]
  [ring.util.response :refer [resource-response response redirect]])
 (use ring.util.response))

(defn- render 
 ([f]
  (render f r/html))
 ([f r]
  (resource-response f {:root r})))

(defn index-page [{{{cookie :value} "ring-session"} :cookies}]
	(render (if cookie "index.html" "landing.html")))

(defn sign-in-page [_]
	(render "login.html"))

(defn sign-up-page [_]
 (render "login.html"))