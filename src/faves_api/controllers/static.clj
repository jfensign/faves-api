(ns faves-api.controllers.static
	(:require 
  [faves-api.config.resources :as r]
  [ring.util.response :refer [resource-response response redirect]])
 (use ring.util.response))

(defn- render 
 ([f]   (render f r/html))
 ([f r] (resource-response f {:root r})))

(defn index-page [{{{cookie :value} "ring-session" :as c} :cookies :as _}]
 (render (if cookie "index.html" "landing.html")))

(defn home [{{{cookie :value} "ring-session" :as c} :cookies :as _}]
	(println cookie)
	(when cookie (render "index.html")))

(defn sign-in-page [_]
 (render "forms/login.html"))

(defn sign-up-page [_]
 (render "forms/register.html"))