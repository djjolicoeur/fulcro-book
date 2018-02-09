(ns app.rest
  (:refer-clojure :exclude [send])
  (:require
   [fulcro.client.logging :as log]
   [fulcro.client.network :as net]
   [cognitect.transit :as ct]
   [goog.events :as events]
   [fulcro.transit :as t]
   [clojure.string :as str]
   [clojure.set :as set]
   [fulcro.client.primitives :as prim])
  (:import
   [goog.net XhrIo EventType]))

(defn make-xhrio [] (XhrIo.))


(defn rekey-post
  [post]
  (set/rename-keys post {"id" :db/id
                         "title" :post/title
                         "userId" :post/user-id
                         "body" :post/body}))

(defn mk-dispatch-fn
  [global-error-callback error-callback status]
  (fn [str error]
    (log/error str)
    (error-callback error)
    (when @global-error-callback
      (@global-error-callback status error))))

(defrecord Network
    [url
     request-transform
     global-error-callback
     complete-app
     transit-handlers]
  net/NetworkBehavior
  (serialize-requests? [this] true)
  net/IXhrIOCallbacks
  (response-ok [this xhr-io valid-data-callback]
    (try
      (let [read-handlers (:read transit-handlers)
            response (.getResponseJson xhr-io)
            edn (js->clj response)
            posts (mapv rekey-post edn)
            fixed-response {:posts posts}]
        (js/console.log :converted-response fixed-response)
        (when (and response valid-data-callback)
          (valid-data-callback fixed-response)))
      (finally (.dispose xhr-io))))
  (response-error [this xhr-io error-callback]
    (try
      (let [status (.getStatus xhr-io)
            log-and-dispatch-error (mk-dispatch-fn global-error-callback
                                                   error-callback
                                                   status)]
        (if (zero? status)
          (log-and-dispatch-error
           (str "NETWORK ERROR: No Connection Established.")
           {:type :network})
          (log-and-dispatch-error
           (str "SERVER ERROR CODE: " status) {})))
      (finally (.dispose xhr-io))))
  net/FulcroNetwork
  (send [this edn ok error]
    (let [xhrio (make-xhrio)
          request-ast (-> (prim/query->ast edn) :children first)
          uri (str "/" (name (:key request-ast)))
          url (str "http://jsonplaceholder.typicode.com" uri)]
      (js/console.log :REQUEST request-ast :URI uri)
      (.send xhrio url "GET")
      (events/listen xhrio
                     (.-SUCCESS EventType)
                     #(net/response-ok this xhrio ok))
      (events/listen xhrio
                     (.-ERROR EventType)
                     #(net/response-error this xhrio error))))
  (start [this] this))


(defn make-rest-network [] (map->Network {}))

