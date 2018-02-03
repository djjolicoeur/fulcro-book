(ns app.api.mutations
  (:require
   [fulcro.client.mutations
    :as mutation
    :refer [defmutation]]
    [fulcro.client.logging :as log]))

;; Place your client mutations here

(defmutation delete-person
  "Deletes person name from list list-name"
  [{:keys [list-id person-id]}]
  (action [{:keys [state]}]
          (let [ident-to-remove [:person/by-id person-id]
                strip-fk (fn [old-fks]
                           (vec (filter #(not= ident-to-remove %) old-fks)))]
            (swap! state update-in
                   [:person-list/by-id list-id :person-list/people] strip-fk))))

