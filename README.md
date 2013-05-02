# clj-rollbar

Clojure client for [Rollbar](https://rollbar.com)

Work in progress.

## Usage

```clojure
(require 'clj-rollbar.core)

; exceptions
(try ("nofn") (catch Exception e (clj-rollbar.core/report-exception "access-token-here" "environment-name" e)))

; log messages
(clj-rollbar.core/report-message "access-token-here" "environment-name" "Something critical happened" "critical")
```

## License

Copyright (C) 2013 Rollbar, Inc.

Distributed under the Eclipse Public License, the same as Clojure.
