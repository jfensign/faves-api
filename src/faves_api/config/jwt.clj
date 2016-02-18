(ns faves-api.config.jwt)

(def pk (get (System/getenv) "JWT_PRIVATE_KEY" false))