(ns aberrant.error-store-test
  (:require [aberrant.error-store :as err-str]
            [clojure.test :refer :all]))

(deftest hash-testing
  (testing "Similar errors should have same hash"
    (is (= (:hash (err-str/create-error "" (Exception.) ""))
           (:hash (err-str/create-error "" (Exception.) ""))))))
