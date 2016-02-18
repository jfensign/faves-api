(ns faves-api.lib.auth.strategy.basic
	(:require 
  [clojure.data.codec.base64 :as base64]
  [clojure.string :as s]
		[faves-api.lib.auth.token.jwt :refer :all]))

(defn- byte-transform
 ^{:private true
   :doc "Used to encode and decode strings.  Returns nil when an exception was raised."}
 [direction-fn string]
 (try
  (apply str (map char (direction-fn (.getBytes string))))
  (catch Exception _)))

(defn- decode-base64
 ^{:private true}
 [^String string]
 (byte-transform base64/decode string))

(defn parse-authorization-string
 ^{:private false} 
 [auth]
 (when-let [cred (and auth (decode-base64 (last (re-find #"^Basic (.*)$" auth))))]
  (into [] (remove nil? (and cred (s/split (str cred) #":" 2))))))

(defn extract-from-headers
 ^{:private false} 
 [{{{auth "authorization" :as headers} :headers} :request}]
 (when auth
  (let [cred-vec (parse-authorization-string auth)]
   (when (= 2 (count cred-vec)) cred-vec))))