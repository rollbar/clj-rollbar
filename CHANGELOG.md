# Changelog

**0.0.3**
- Added ability to report request information with exceptions. Via [#4](https://github.com/rollbar/clj-rollbar/pull/4)
- Timeouts increased to 30s to work better on slow connections.

**0.0.2**
- Added preliminary support for exception causes. Exceptions with causes add two shim frames to the stacktrace that is reported to Rollbar. Proper support will require changes to the Rollbar API.
