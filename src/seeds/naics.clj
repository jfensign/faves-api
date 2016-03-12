(ns seeds.naics
  (:require
    [faves-api.models 
      [industry :as i] 
      [industry-group :as ig] 
      [industry-sub-sector :as iss] 
      [industry-sector :as is]])
  (:use 
    [korma core db] 
    [clojure.java io]))

(def naics-file (file (resource "csv/naics.csv")))

(defn naics-create [suffix & args]
  (let [id (clojure.string/split (:id (first args)) #"-")]
    (doseq [cat (clojure.string/split (:id (first args)) #"-") 
      :let [a (assoc (first args) :id cat)]
            f (->> "create" symbol (ns-resolve (symbol (format "%s.%s" "faves-api.models" suffix))))]
      (f a))))

(defn naics-rank [[cat n]]
  (let [op   (fn [cnt] 
                  (and 
                    (= cnt (count cat))
                    (= 1 (count (clojure.string/split cat #"-")))))
            rank (cond
                  (op 3) ["industry-sub-sector" "industry_sub_sectors"]
                  (op 4) ["industry-group" "industry_groups"]
                  (op 5) ["industry" "industries"]
                  :else  ["industry-sector" "industry_sectors"])]
        [cat n rank]))


(doseq [row (map #(clojure.string/split % #";") (-> naics-file slurp (clojure.string/split #"\n"))) 
            :let [[cat n [rank table sep]] (naics-rank row)
                  [pcat pn [prank ptable psep]] (naics-rank [(subs cat 0 (- (count cat) 1)) n])]
            :when rank]
      (try
        (let [a (merge {:id cat :name n} (if (and prank (not= "industry-sector" rank)) (assoc {} (keyword (format "%s_id" ptable)) pcat)))]
          (naics-create rank a))
        (catch Exception _ 
          (-> _ .getMessage println))))