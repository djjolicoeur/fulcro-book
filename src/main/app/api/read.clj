(ns app.api.read
  (:require
    [fulcro.server :refer [defquery-entity defquery-root defmutation]]
    [taoensso.timbre :as timbre]))

(def people-db (atom {1 {:db/id 1
                         :person/name "Bert"
                         :person/age 55
                         :person/relation :friend}
                      2 {:db/id 2
                         :person/name "Sally"
                         :person/age 22
                         :person/relation :friend}
                      3 {:db/id 3
                         :person/name "Allie"
                         :person/age 76
                         :person/relation :enemy}
                      4 {:db/id 4
                         :person/name "Zoe"
                         :person/age 32
                         :person/relation :friend}
                      99 {:db/id 99
                          :person/name "Me"
                          :person/role "admin"}}))

(defn get-people
  [kind keys]
  (->> @people-db
       vals
       (filter #(= kind (:person/relation %)))
       vec))


;; Server queries can go here


(defquery-root :current-user
  (value [env params]
    (get @people-db 99)))

(defquery-root :my-friends
  (value [{:keys [query]} params]
         (get-people :friend query)))

(defquery-root :my-enemies
  (value [{:keys [query]} params]
         (get-people :enemy query)))

(defquery-entity :person/by-id
  (value [env id params]
         (update (get @people-db id) :person/name str " (refreshed)")))
