(ns aberrant.error-store
  (require [aberrant.error :as ab-error]))

(def retry-delay-miliseconds 2000)

;; Default number of exceptions to buffer in memory i the event of an
;; error store outage.
(def default-backup-queue-size 1000)

;; The default number of seconds to roll up errors for. Identical
;; stacktrace errors within 10 minutes get the duplicate count
;; increased instead of a new exception being logged.
(def default-rollup-seconds 600)

(defprotocol PErrorStore
  "PErrorStore is how errors are stored."
  (log-error! [this error]
             "Logs an error for the application.")
  (get-error [this guid]
             "Retrieves a single error based on Id.")
  (protect-error [this guid]
                 "Prevents error identified by 'id' from being deleted
                 when the error log is full, if the store supports
                 it.")
  (delete-error! [this guid]
                "Deletes a specific error from the log.")
  (delete-all-errors! [this]
                     "Deletes all non-protected errors form the log.")
  (get-error-count [this] [this since]
                   "Returns a count of application errors from all
                   time, or, if given a date, since that date.")
  (get-all-errors [this application-name]
                  "Returns a sequence of all the errors in the error store."))

(defn log-exception
  "Logs an exception to the given error store."
  [error-store ex context]
  (let [new-error (ab-error/create-error "" ex context)]
    (log-error! error-store new-error)))
