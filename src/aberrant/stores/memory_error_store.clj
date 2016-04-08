(ns aberrant.stores.memory-error-store
  (:require [aberrant.error-store :refer :all]))

(def maximum-size 500)
(def default-size 200)

(defrecord MemoryErrorStore [errors])

(defn- remove-error-with-guid
  "Given a guid and a sequence, remove the given error with that guid
  from the sequence."
  [coll guid]
  (into [] (filter #(not= (:guid %) guid) coll)))

(defn- remove-unprotected-errors
  "Given a sequence of errors, remove any that aren't protected."
  [coll]
  (into [] (filter #(= (:protected %) true) coll)))

(defn remove-first
  "Remove the first matching item in the sequence, then return."
  [f [head & tail]]
  (into []
        (if (f head)
          tail
          (cons head (lazy-seq (remove-first f tail))))))

(defn remove-first-unprotected
  "Remove the first error from the sequence that's unprotected."
  [coll]
  (into [] (remove-first #(not (:protected %)) coll)))

(extend-type MemoryErrorStore
  PErrorStore
  (log-error! [this error]
    (if (>= (count @(:errors this)) (:maximum-size this))
      (swap! (:errors this) remove-first-unprotected))
    (swap! (:errors this) conj error)
    this)
  (get-error [this guid]
    (first (filter #(= guid (:guid %)) @(:errors this))))
  (protect-error [this guid]
    (println "test"))
  (delete-error! [this guid]
    (swap! (:errors this) remove-error-with-guid guid)
    this)
  (delete-all-errors! [this]
    (swap! (:errors this) remove-unprotected-errors)
    this)
  (get-error-count
    ([this] (count @(:errors this)))
    ([this _] nil))
  (get-all-errors [this _]
    (if-let [errors (:errors this)]
      @errors)))

(defn in-memory-store
  "Creates an in-memory error store."
  []
  (merge (MemoryErrorStore. (atom []))
         {:maximum-size maximum-size}))
