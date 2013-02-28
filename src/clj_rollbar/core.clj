(ns clj-rollbar.core
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]))

(def- endpoint
  "Rollbar API endpoint base URL"
  "https://api.rollbar.com/api/1/item/")

(defn- build-payload
  [access-token data]
  (json/write-str {"access_token" access-token :data data}))

(defn- send-payload
  [payload]
  (client/post endpoint
    {:body payload
     :content-type :json
     :socket-timeout 1000
     :conn-timeout 1000
     :accept :json}))
  
(defn report-message
  "Reports a simple string message at the specified level"
  [access-token environment message-body level]
  (send-payload (build-payload access-token 
    ; data follows
    {:environment environment
     :level level
     :body {
       :message {
         :body message-body
       }
     }
     :notifier {
       :name "clj-rollbar"
       :version "0.0.1"
     }
    })))
