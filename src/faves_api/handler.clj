(ns faves-api.handler
  (:require [liberator.core :refer [resource defresource]]
  			    [compojure.handler :as handler]
  			    [ring.middleware.params :refer [wrap-params]]
  			    [ring.middleware.cors :refer [wrap-cors]]
  			    [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
  			    [clojure.data.json :as json]
  			    [ring.util.response :refer [resource-response response]]
  	        [compojure.core :refer :all]
  	        [faves-api.controllers.user :as user]
            [faves-api.controllers.favor :as favor]
            [faves-api.controllers.register :as register]
            [faves-api.controllers.authenticate :as authenticate]
            [base64-clj.core :as base64])
  (use ring.middleware.basic-authentication))

(extend-type java.sql.Timestamp
  json/JSONWriter
  (-write [date out]
  (json/-write (str date) out)))

(defn auth-middleware [app]
 (fn [request]
   (let [{:keys [headers query]} request
         bearer (headers "authorization")
         no-auth-res {:status 401 :body {:error "Unauthorized"}}]
    (println (-> request :uri))
    (if bearer
      (let [token (base64/decode (last (clojure.string/split bearer #"\s+")))
            user  (user/token-auth token)]
        (if user
          (app (conj request {:user user}))
          no-auth-res))
      no-auth-res))))

(defn json-parse-body [app]
  (fn [request]
    (let [{:keys [headers request-method body]} request]
      (println (keys headers))
      (app 
        (if (#{:put :post} request-method)
          (assoc request :body (clojure.walk/keywordize-keys (json/read-str (slurp body))))
          request)))))

(defroutes api-routes
  (context "/api/v1" []
    (ANY "/" [] (resource))
    (ANY "/me" [] println)
    (ANY "/users" [] user/describe)
    (ANY "/users/:id{[0-9]+}" [id] (user/select id))
    (ANY "/favors" [] favor/base)))

(defroutes registration-routes
  (context "/register" []
    (ANY "/:provider" [provider] register/signup))
  (context "/registrations/v1" []
    (ANY "/users" [] register/signup)))

(defroutes login-routes
  (context "/authenticate/v1" []
    (ANY "/:provider" [provider] authenticate/authorize))
  (context "/login/v1" []
    (ANY "/" [] authenticate/authorize)
    (ANY "/:provider" [provider] authenticate/authorize)))

(def app
  (routes

    (-> registration-routes
        json-parse-body
        (handler/api))

    (-> login-routes
        json-parse-body
        (handler/api))

    (-> api-routes
        auth-middleware
        wrap-json-response
        json-parse-body
        (handler/api))))