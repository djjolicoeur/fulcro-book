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

(defn sort-friends-by*
  [state-map field]
  (let [friend-idents (get-in state-map
                              [:person-list/by-id
                               :friends
                               :person-list/people]
                              [])
        friends (map (fn [friend-ident]
                       (get-in state-map friend-ident)) friend-idents)
        sorted-friends (sort-by field friends)
        new-idents (mapv
                    (fn [friend]
                      [:person/by-id (:db/id friend)])
                    friends)]
    (assoc-in state-map
              [:person-list/by-id :friends :person-list/people]
              new-idents)))

(defmutation sort-friends
  "Sort friends by age"
  [no-params]
  (action [{:keys [state]}]
          (swap! state sort-friends-by* :person/age)))

