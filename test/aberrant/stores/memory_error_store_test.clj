(ns aberrant.stores.memory-error-store-test
  (:require [aberrant.stores.memory-error-store :refer [in-memory-store]]
            [aberrant.error-store :refer :all]
            [clojure.test :refer :all]))

(deftest adding-an-error
  (testing "Orders should be added in order."
    (let [error-store (in-memory-store)]
      (log-error! error-store {:guid "1"})
      (log-error! error-store {:guid "2"})
      (log-error! error-store {:guid "3"})
      (is (= [{:guid "1"} {:guid "2"} {:guid "3"}]
             (get-all-errors error-store "")))))
  (testing "Oldest errors should be removed first if we have too many."
    (let [error-store (assoc (in-memory-store) :maximum-size 2)]
      (log-error! error-store {:guid "1"})
      (log-error! error-store {:guid "2"})
      (log-error! error-store {:guid "3"})
      (is (= [{:guid "2"} {:guid "3"}]
             (get-all-errors error-store "")))))
  (testing "Protected errors should not be removed."
    (let [error-store (assoc (in-memory-store) :maximum-size 2)]
      (log-error! error-store {:guid "1" :protected true})
      (log-error! error-store {:guid "2"})
      (log-error! error-store {:guid "3"})
      (is (= [{:guid "1" :protected true} {:guid "3"}]
             (get-all-errors error-store ""))))))

(deftest deleting-all-errors
  (testing "Deleting all errors should remove all unprotected errors."
    (let [error-store (in-memory-store)]
      (log-error! error-store {:guid "1"})
      (log-error! error-store {:guid "2"})
      (log-error! error-store {:guid "3"})
      (delete-all-errors! error-store)
      (is (empty? (get-all-errors error-store "")))))
  (testing "Protected errors can't be deleted."
    (let [error-store (in-memory-store)]
      (log-error! error-store {:guid "1"})
      (log-error! error-store {:guid "2" :protected true})
      (log-error! error-store {:guid "3" :protected false})
      (delete-all-errors! error-store)
      (is (= [{:guid "2" :protected true}]
             (get-all-errors error-store ""))))))
