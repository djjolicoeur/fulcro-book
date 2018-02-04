(ns app.client
  (:require [fulcro.client :as fc]
            [fulcro.client.data-fetch :as df]
            [app.ui.root :as root]
            [app.api.mutations :as api]))

(defonce app (atom (fc/new-fulcro-client
                    :started-callback
                    (fn [app]
                      (df/load app :current-user root/Person)
                      (df/load app
                               :my-enemies
                               root/Person
                               {:target [:person-list/by-id
                                         :enemies
                                         :person-list/people]})
                      (df/load app
                               :my-friends
                               root/Person
                               {:target [:person-list/by-id
                                         :friends
                                         :person-list/people]
                                :post-mutation `api/sort-friends})))))
