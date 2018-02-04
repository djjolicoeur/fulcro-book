(ns app.intro
  (:require [fulcro.client.cards :refer [defcard-fulcro]]
            [app.api.mutations :as api]
            [app.ui.root :as root]
            [app.ui.components :as comp]
            [fulcro.client.data-fetch :as df]))

;; (defcard SVGPlaceholder
;;   "# SVG Placeholder"
;;   (comp/ui-placeholder {:w 200 :h 200}))

(defcard-fulcro sample-app
  root/Root
  {}
  {:inspect-data true
   :fulcro
   {:started-callback
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
                :post-mutation `api/sort-friends}))}})
