(ns aberrant.error
  (:require [clojure.string :as clj-str])
  (:import [java.util UUID]))

(defn get-hash
  "Generate a unique hash so we can have a quick lookup for duplicate
  errors. Strips out any numbers from the $eval details when hashing."
  [settings detail machine-name]
  (if-not (empty? detail)
    (let [stripped-detail (clj-str/replace detail #"\$eval[\d]+" "")]
      (if-not (empty? machine-name)
        (bit-xor (* (.hashCode stripped-detail) 397)
                 (.hashCode machine-name))))))

(defn create-error
  [settings exception context]
  (let [application-name nil
        machine-name (.getHostName (java.net.InetAddress/getLocalHost))
        detail (apply str (interpose "\n" (.getStackTrace exception)))]
    {:guid (UUID/randomUUID)
     :application-name application-name
     :machine-name machine-name
     :ex-type (.getName (.getClass exception))
     :message (.getMessage exception)
     :detail detail
     :creation-date (java.util.Date.)
     :duplicate-count 1
     :error-hash (get-hash settings detail machine-name)}))
