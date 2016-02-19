(ns faves-api.lib.route.register.v1
 (:require
  [compojure.core :refer :all]
  [compojure.route :as route]
  [faves-api.controllers
   [register :as register]]))

(defroutes config
 (context "/registrations/v1" []
  (ANY "/" [] register/signup)))