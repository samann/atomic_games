(ns othello.t-core
  (:require [midje.sweet :refer :all]
            [othello.core :refer :all]))

(fact "Check valid options"
  (invalid-options? {}) => true
  (invalid-options? {:white "foo"}) => true
  (invalid-options? {:black "foo"}) => true
  (invalid-options? {:white "foo" :black "foo"}) => false)