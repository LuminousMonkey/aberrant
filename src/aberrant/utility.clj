(ns aberrant.utility
  (:require [clojure.string :as clj-str])
  (:import [java.util UUID]))

(def ipv4-regex #"\b([0-9]{1,3}\.){3}[0-9]{1,3}$")

(def unknown-ip "0.0.0.0")

(defn truncate
  "Truncate a string to be maxlen or smaller."
  [s n]
  (subs s 0 (min (count s) n)))

(defn truncate-with-ellipsis
  "If string is over n, returns with a new string of length n, with
  ... as the final characters."
  [s n]
  (let [ellipsis "..."]
    (if (and (not (empty? s)) (> (count s) n))
      (str (truncate s (- n (count ellipsis))) ellipsis)
      s)))

(defn private-ip?
  "Return true if the given string represents a private IPV4 address."
  [address]
  (or (.startsWith address "192.168.")
      (.startsWith address "10.")
      (.startsWith address "127.0.0.")))

(defn get-remote-ip
  "Retrieves the IP address of the current request."
  [request]
  (let [ip-addr (:remote-addr request)
        ip-forwarded (get-in request [:headers "x-forwarded-for"])]
    (if (and ip-forwarded (not (private-ip? ip-forwarded)))
      ip-forwarded
      (if-not (empty? ip-addr) ip-addr unknown-ip))))

(defn to-guid
  "Converts a string to a GUID, or empty GUID if empty or invalid."
  [s]
  (try
    (UUID/fromString s)
    (catch IllegalArgumentException e
      (UUID. 0 0))))

(defn guid-to-filename
  "Given a guid, strip of non-essential characters."
  [guid]
  (clj-str/replace guid #"-" ""))

(defn to-short-exception
  "Gets the short exception name."
  [exception-name]
  (if-not (empty? exception-name)
    (let [short-type (last (clj-str/split exception-name #"\."))
          suffix "Exception"]
      (if (and (.endsWith short-type suffix) (not= short-type suffix))
        (subs short-type 0 (- (count short-type) (count suffix)))
        short-type))
    ""))
