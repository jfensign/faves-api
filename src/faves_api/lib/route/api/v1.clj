(ns faves-api.lib.route.api.v1
	(:require
		[compojure.core :refer :all]
		[compojure.route :as route]
		[faves-api.controllers
  [user :as user]
  [industry-sector :as industry-sector]
  [industry-sub-sector :as industry-sub-sector]
  [industry-group :as industry-group]
  [industry :as industry]]))

(defroutes config
 (context "/api/v1" []
  (context "/users" []
   (ANY "/" [] user/describe))
  (context "/industries" []
   (ANY "/" [] industry/base)
   (ANY "/:id" [id] industry/base))
  (context "/industry-sectors" []
   (ANY "/" [] industry-sector/base)
   (ANY "/:id" [id] industry-sector/base))
  (context "/industry-sub-sectors" []
   (ANY "/" [] industry-sub-sector/base)
   (ANY "/:id" [id] industry-sub-sector/base))
  (context "/industry-groups" []
   (ANY "/" [] industry-group/base)
   (ANY "/:id" [id] industry-group/base)))
 (route/not-found {:status 404 :body {:error "Not Found"}}))