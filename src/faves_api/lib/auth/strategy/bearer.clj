(ns faves-api.lib.auth.strategy.bearer
	(:require 
		[faves-api.lib.auth.token.jwt :refer :all]))

(defn extract-from-headers [{{token :x-auth-token} :headers :as _}]
	token)