# clj-rollbar

Clojure client for [Rollbar](https://rollbar.com)

Work in progress.

## Usage

```clojure
(require '[clj-rollbar.core :as rollbar])

; exceptions
(try
  ("nofn")
  (catch Throwable e
    (rollbar/report-exception "access-token" "environment-name" e)))

; log messages
(rollbar/report-message "access-token" "environment-name"
                        "Something critical happened" "critical")
```

You can also add information from a Ring HTTP request map, like so:

```clojure
(rollbar/report-exception "access-token" "environment-name" e
                          :request the-ring-request-map)))
```

You can use this feature with some ring middleware, like so:

```clojure
(defn default-handler
  [request]
  {:status 204})

(defn wrap-error-page
  "Ring middleware"
  [handler]
  (fn [request]
    (try
      (handler request)
      (catch Throwable e
        (rollbar/report-exception "access-token" "environment-name" e
                                  :request request)))))

(def app (wrap-error-page default-handler))
```

One caveat: the request body cannot be read from the request map more than
once, unless special care is taken.  See [this Stack Overflow
question](http://stackoverflow.com/q/20553899/202292) for more information.

## License

Copyright (C) 2013 Rollbar, Inc.

Distributed under the Eclipse Public License, the same as Clojure.
