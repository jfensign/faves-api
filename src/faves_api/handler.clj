(ns faves-api.handler
 (:require 
  [compojure.handler :as handler]
  [ring.middleware.params :refer [wrap-params]]
  [ring.middleware.keyword-params :refer [wrap-keyword-params]]
  [ring.middleware.cors :refer [wrap-cors]]
  [ring.middleware.json :refer [wrap-json-response wrap-json-body wrap-json-params]]
  [com.duelinmarkers.ring-request-logging :refer [wrap-request-logging]]
  [clojure.data.json :as json]
  [ring.util.response :refer [resource-response response]]
  [compojure.core :refer :all]
  [compojure.route :as route]
  [faves-api.controllers.authenticate :as authenticate]
  [faves-api.controllers.register :as register]
  [faves-api.controllers.user :as user]
  [faves-api.controllers.industry-sector :as industry-sector]
  [faves-api.controllers.industry :as industry]
  [base64-clj.core :as base64])
 (use ring.middleware.basic-authentication ring.middleware.token-authentication clojure.java.io))

(extend-type java.sql.Timestamp
 json/JSONWriter
 (-write [date out]
 (json/-write (str date) out)))

(defroutes registration-routes
 (context "/register" []
  (ANY "/:provider" [provider] register/signup))
 (context "/registrations/v1" []
  (ANY "/" [] register/signup)
  (ANY "/users" [] register/signup)))

(defroutes login-routes
 (context "/authenticate/v1" []
  (ANY "/:provider" [provider] authenticate/authorize))
 (context "/login/v1" []
  (ANY "/" [] authenticate/authorize)
  (ANY "/:provider" [provider] authenticate/authorize)))

(defroutes api-routes
 (context "/api/v1" []
  (context "/users" []
   (ANY "/" [] user/describe))
  (context "/industries" []
   (ANY "/" [] industry/base)
   (ANY "/:id" [id] industry/base))
  (context "/industry-sectors" []
   (ANY "/" [] industry-sector/base)
   (ANY "/:id" [id] industry-sector/base)))
 (route/not-found {:status 404 :body {:error "Not Found"}}))


(def app
 (routes

  (-> registration-routes
      (wrap-routes wrap-request-logging)
      (wrap-routes wrap-json-response)
      (wrap-routes wrap-keyword-params)
      (wrap-routes wrap-json-body {:keywords? true :bigdecimals? true}))

  (-> login-routes
      (wrap-routes wrap-request-logging)
      (wrap-routes wrap-json-response)
      (wrap-routes wrap-keyword-params)
      (wrap-routes wrap-json-body {:keywords? true :bigdecimals? true}))

  (-> api-routes
      (wrap-routes wrap-request-logging)
      (wrap-routes wrap-json-response)
      (wrap-routes wrap-keyword-params)
      (wrap-routes wrap-json-body {:keywords? true :bigdecimals? true}))))