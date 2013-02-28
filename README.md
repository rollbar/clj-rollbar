# clj-rollbar

Clojure client for [Rollbar](https://rollbar.com)

Work in progress.

## Usage

```clojure
(require 'clj-rollbar.core)
(clj-rollbar.core/report-message "access-token-here" "environment-name" "Something critical happened" "critical")
```

## License

Copyright (C) 2013 Rollbar, Inc.

Distributed under the Eclipse Public License, the same as Clojure.
