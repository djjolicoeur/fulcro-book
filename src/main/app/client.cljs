(ns app.client
  (:require [fulcro.client :as fc]
            [fulcro.client.data-fetch :as df]
            [app.rest :as rest]
            [fulcro.client.network :as net]
            [app.ui.root :as root]
            [app.api.mutations :as api]))

(defonce app (atom (fc/new-fulcro-client
                    :networking
                    {:remote (net/make-fulcro-network
                              "/api"
                              :global-error-callback (constantly nil))
                     :rest (rest/make-rest-network)}
                    :started-callback
                    (fn [app]
                      (df/load app :current-user root/Person)
                      (df/load app
                               :posts
                               root/Post
                               {:remote :rest
                                :target [:post-list/by-id :the-one :posts]})
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
