(ns clj-rollbar.core
  (:use (clj-stacktrace [core :only [parse-exception]] [repl :only [method-str]]))
  (:use [clojure.string :only [split]])
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]))

(def endpoint
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

(defn- base-data
  [environment level]
  {
    :environment environment
    :level level
    :timestamp (int (/ (System/currentTimeMillis) 1000))
    :language "clojure"
    :notifier {
      :name "clj-rollbar"
      :version "0.0.1"
    }
    ; TODO uuid
  })
  
(defn report-message
  "Reports a simple string message at the specified level"
  [access-token environment message-body level]
  (send-payload (build-payload access-token 
    (merge (base-data environment level)
      {:body {
        :message {
          :body message-body
        }
      }}))))

(def ^:private shim-name "shim-frame.clj")

(defn- get-shim-frames
  [parsed-exception]
  [ { :lineno 0
      :filename shim-name
      :method "causedBy"
    }
    {
      :lineno 0
      :filename shim-name
      :method (str (:class parsed-exception))
    }])

(defn- project-frame
  [stackframe]
  (let [{:keys [file line], :as elem} stackframe] 
    {
      :lineno line 
      :filename file 
      :method (method-str elem)
    }))

(defn- project-exception-to-frames 
  [parsed-exception]
  (vec (map project-frame (:trace-elems parsed-exception))))

(defn- build-frames-with-causes
  [parsed-exception]
  (loop [cur-exception parsed-exception 
         frames []]
    (if (:cause cur-exception)
      (recur (:cause parsed-exception) 
             (concat frames (project-exception-to-frames cur-exception) (get-shim-frames parsed-exception)))
      (concat frames (project-exception-to-frames cur-exception)))))

(defn- build-trace
  [parsed-exception]
  {
    :exception {
      :class (first (split (str (get parsed-exception :class)) #":"))
      :message (get parsed-exception :message)
    }
    :frames (build-frames-with-causes parsed-exception)
  })
    
(defn report-exception
  "Reports an exception at the 'error' level"
  [access-token environment exception]
  (send-payload (build-payload access-token
    (merge (base-data environment "error")
      {:body {
        :trace (build-trace (parse-exception exception))
      }}))))
