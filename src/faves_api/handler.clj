(ns faves-api.handler
 (:require 
  [faves-api.lib.route.api
   [v1 :as api-v1]]
  [faves-api.lib.route.auth
   [v1 :as auth-v1]]
  [faves-api.lib.route.register
   [v1 :as register-v1]]
  [faves-api.lib.route.static 
   [v1 :as static-v1]])
 (use 
  compojure.core
  com.duelinmarkers.ring-request-logging
  ring.middleware.json
  ring.middleware.keyword-params
  ring.middleware.params
  ring.middleware.session 
  ring.middleware.file-info 
  ring.middleware.file 
  ring.middleware.basic-authentication 
  ring.middleware.token-authentication
  faves-api.lib.extension.java.sql.Timestamp)
 (:gen-class))

(def app
 (routes
  (-> static-v1/config   
      (wrap-routes wrap-session {:cookie-attrs {:max-age 3600}})
      (wrap-file "resources")
      (wrap-routes wrap-file-info))
  (-> register-v1/config
      (wrap-routes wrap-request-logging)
      (wrap-routes wrap-json-response)
      (wrap-routes wrap-keyword-params)
      (wrap-routes wrap-session {:cookie-attrs {:max-age 3600}})
      (wrap-routes wrap-json-body {:keywords? true :bigdecimals? true}))
  (-> auth-v1/config
      (wrap-routes wrap-request-logging)
      (wrap-routes wrap-json-response)
      (wrap-routes wrap-keyword-params)
      (wrap-routes wrap-session {:cookie-attrs {:max-age 3600}})
      (wrap-routes wrap-json-body {:keywords? true :bigdecimals? true}))
  (-> api-v1/config
      (wrap-routes wrap-request-logging)
      (wrap-routes wrap-json-response)
      (wrap-routes wrap-keyword-params)
      (wrap-routes wrap-session {:cookie-attrs {:max-age 3600}})
      (wrap-routes wrap-json-body {:keywords? true :bigdecimals? true}))))