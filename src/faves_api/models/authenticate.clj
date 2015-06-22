(ns faves-api.models.authenticate
  (:require [faves-api.database :refer [users authentications]]
            [clj-http.client :as http]
            [slingshot.slingshot :as slingshot]
            [base64-clj.core :as b64]
            [clojure.data.json :as json]
            [faves-api.config.provider :as p])
  (:use (korma core)))

(def ^{:private true
        :doc "Field definitions."}
   editable-fields
      {:access_token  {:unique true}
       :provider     {:unique true}
       :provider_uuid {:unique false}
       :remote_addr  {:unique false}
       :user_id  {:unique false}})


(defn ^{:private false
       :doc "Resolves the auth function to call in faves-api.models.authenticate ns. 
             Uses the URL parameter :provider"}
    resolve-auth-fn [provider & opts]
    (when-let [function (->> provider
                             symbol 
                             (ns-resolve (symbol "faves-api.models.authenticate")))]
      (apply function opts)))

(defn- parse-provider-response [res]
    (if (instance? clojure.lang.PersistentArrayMap res)
        nil
        (or (into {} (for [[k v] (json/read-str res)] [(keyword k) v])) nil)))

(defmacro fmt [^String string]
  (let [-re #"#\{(.*?)\}"
        fstr (clojure.string/replace string -re "%s")
        fargs (map #(read-string (second %)) 
                   (re-seq -re string))]
    `(format ~fstr ~@fargs)))

(defn find-by-provider-token [provider token]
  (first 
    (select authentications
            (join users (= :users.id :user_id))
            (where {:provider provider :token token}))))

(defn find-by-provider-user-id [provider qs]
  (let [profile (resolve-auth-fn provider qs)]
    (first
      (select authentications
              (join users (= :users.id :user_id))
              (where {:provider provider :provider_uuid (:provider_uuid profile)})))))

(defn create [authentication]
  (let [sanitized (select-keys authentication (keys editable-fields))]
    (insert authentications (values sanitized))))

(defn http-request
  "HTTP error handling"
  [req]
  (slingshot/try+
    (do
      (let [res (req)
            body (:body res)]
      (parse-provider-response body)))
    (catch [] 
      {:keys [status body]} 
      {:body body :status status})))

(defn facebook
  "Verfiy that the user's request token is valid."
  ([qs & opts]
    (when-let [prof (http-request 
                (fn [] 
                  (http/get (:auth-uri p/facebook) {:query-params qs 
                                                    :throw-entire-message? true})))]
      (clojure.set/rename-keys prof (:key-mapping p/facebook)))))

(defn google
  "Verify user is authenticated"
  [qs]
  (http-request 
    (fn [] (http/get (:auth-uri p/google) {:query-params qs
                                           :throw-entire-message? true}))))

(defn twitter
  "Verify user is authenticated"
  [qs]
    (http-request 
      (fn [] 
        (let [token (get qs "access_token")]
          (http/get (:auth-uri p/twitter) {:headers {"Authorization" (fmt "Bearer #{token}") 
                                                     "Host" "api.twitter.com" 
                                                     "Accept-Encoding" "gzip"}
                                           :throw-entire-message? true})))))

(defn linkedin
  "Verify user is authenticated"
  [qs]
  (http-request 
    (fn [] 
      (let [token (get qs "access_token")]
        (http/get (:auth-uri p/linkedin) {:headers {"Authorization" (fmt "Bearer #{token}")}
                                          :query-params {:format "json"}
                                          :throw-entire-message? true})))))

(defn github
  "Verify user is authenticated"
  [qs]
  (let [authorization-endpoint "https://api.github.com/user"
        token (get qs "access_token")]
      (http-request 
        (fn [] 
          (http/get authorization-endpoint {:headers {:Accept "application/vnd.github.v3+json"
                                                      :Authorization (fmt "token #{token}")}
                                            :query-params {"scopes" "[user, repo]"}
                                            :throw-entire-message? true})))))



(defn reddit
  "Verify user is authenticated"
  [qs]
  (let [authorization-endpoint "https://oauth.reddit.com/api/v1/me"
        token (get qs "access_token")]
    (http-request 
      (fn [] 
        (http/get authorization-endpoint {:headers {"Authorization" (fmt "bearer #{token}")}
                                          :throw-entire-message? true})))))