(ns app.intro
  (:require [fulcro.client.cards :refer [defcard-fulcro]]
            [app.ui.root :as root]
            [app.ui.components :as comp]))

;; (defcard SVGPlaceholder
;;   "# SVG Placeholder"
;;   (comp/ui-placeholder {:w 200 :h 200}))

(defcard-fulcro sample-app
  root/Root
  {}
  {:inspect-data true})
