(ns app.api.mutations
  (:require
   [fulcro.client.mutations
    :as mutation
    :refer [defmutation]]
    [fulcro.client.logging :as log]))

;; Place your client mutations here

(defmutation delete-person
  "Deletes person name from list list-name"
  [{:keys [list-name name]}]
  (action [{:keys [state]}]
          (let [path (if (= "Friends" list-name)
                       [:friends :person-list/people]
                       [:enemies :person-list/people])
                old-list (get-in @state path)
                new-list (vec (filter
                               #(not= name (:person/name %)) old-list))]
            (swap! state assoc-in path new-list))))

