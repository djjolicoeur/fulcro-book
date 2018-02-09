(ns app.api.mutations
  (:require
   [app.api.read :refer [people-db]]
   [taoensso.timbre :as timbre]
   [fulcro.server :refer [defmutation]]
   [fulcro.client.impl.application :as app]))

;; Place your server mutations here


(defmutation delete-person
  [{:keys [person-id]}]
  (action
   [{:keys [state]}]
   (timbre/info :task ::delete-person :person-id person-id)
   (swap! people-db dissoc person-id)))
