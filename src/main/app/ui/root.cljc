(ns app.ui.root
  (:require
    [fulcro.client.mutations :as m]
    [fulcro.client.data-fetch :as df]
    translations.es                                         ; preload translations by requiring their namespace. See Makefile for extraction/generation
    [fulcro.client.dom :as dom]
    [app.api.mutations :as api]
    [app.ui.components :as components]
    [fulcro.client.primitives :as prim :refer [defsc]]
    [fulcro.i18n :refer [tr trf]]))

;; The main UI of your application

(defsc Person
  [this {:keys [db/id person/name person/age]} {:keys [onDelete]}]
  {:query [:db/id :person/name :person/age]
   :ident [:person/by-id :db/id]
   :initial-state
   (fn [{:keys [id name age] :as params}]
     {:db/id id :person/name name :person/age age})}
  (dom/li nil
          (dom/h5 nil name (str "(age: " age ")")
                  (dom/button #js {:onClick #(onDelete id)} "X"))))

(def ui-person (prim/factory Person {:keyfn :person/name}))

(defsc PersonList
  [this {:keys [db/id person-list/label person-list/people]}]
  {:query [:db/id :person-list/label
           {:person-list/people (prim/get-query Person)}]
   :ident [:person-list/by-id :db/id]
   :initial-state
   (fn [{:keys [id label]}]
     {:db/id id
      :person-list/label label
      :person-list/people []})}
  (let [delete-person (fn [person-id]
                        (prim/transact! this
                                        `[(api/delete-person
                                           {:list-id ~id
                                            :person-id ~person-id})]))]
      (dom/div nil
               (dom/h4 nil label)
               (dom/ul nil
                       (map (fn [p]
                              (ui-person
                               (prim/computed p
                                              {:onDelete delete-person})))
                            people)))))

(def ui-person-list (prim/factory PersonList))


(defsc Root
  [this {:keys [ui/react-key friends enemies current-user]}]
  {:query [:ui/react-key
           {:current-user (prim/get-query Person)}
           {:friends (prim/get-query PersonList)}
           {:enemies (prim/get-query PersonList)}]
   :initial-state
   (fn [params]
     {:friends (prim/get-initial-state PersonList
                                       {:id :friends :label "Friends"})
      :enemies (prim/get-initial-state PersonList
                                       {:id :enemies :label "Enemies"})})}
  (dom/div #js {:key react-key}
           (dom/h4 nil (str "Current User: " (:person/name current-user)))
           (ui-person-list friends)
           (ui-person-list enemies)))




