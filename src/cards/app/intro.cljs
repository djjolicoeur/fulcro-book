(ns app.intro
  (:require [fulcro.client.cards :refer [defcard-fulcro]]
            [app.api.mutations :as api]
            [app.ui.root :as root]
            [app.ui.components :as comp]
            [app.rest :as rest]
            [fulcro.client.data-fetch :as df]
            [fulcro.client.network :as net]))

;; (defcard SVGPlaceholder
;;   "# SVG Placeholder"
;;   (comp/ui-placeholder {:w 200 :h 200}))

(defcard-fulcro sample-app
  root/Root
  {}
  {:inspect-data false
   :fulcro
   {:networking
    {:remote (net/make-fulcro-network "/api"
                                      :global-error-callback (constantly nil))
     :rest (rest/make-rest-network)}
    :started-callback
    (fn [app]
      (df/load app :posts root/Post {:remote :rest
                                :target [:post-list/by-id :the-one :posts]})
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
                :post-mutation `api/sort-friends}))}})
