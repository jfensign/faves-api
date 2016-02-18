(ns faves-api.controllers.common)

(def base-resource
  "Base for all resources.

   Due to the way liberator's resources merge, these base definitions
   define a bunch of content types, even if the resources that inherit
   from them don't. The defaults are here to provide reasonable text
   error messages, instead of returning big slugs of html."
  (let [not-found (comp rep/ring-response
                        (route/not-found "Route not found!"))
        base {"text/html" not-found}]
    {:handle-not-acceptable
     (->> {"application/json" {:success false
                               :message "No acceptable resource available"}
           "text/plain" "No acceptable resource available."}
          (with-default "text/plain")
          (media-typed base))

     :handle-not-found
     (->> {"application/json" {:success false
                               :message "Resource not found."}
           "text/plain" "Resource not found."}
          (with-default "text/plain")
          (media-typed base))}))