(ns othello.random-player.t-core
  (:require [midje.sweet :refer :all]
            [othello.random-player.core :refer :all]))

(fact "Check valid options"
  (invalid-options? {}) => true
  (invalid-options? {:player ...player...}) => true
  (invalid-options? {:board ...board...}) => true
  (invalid-options? {:board ...board... :player ...player...}) => false)