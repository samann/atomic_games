(ns othello.random-player.t-player
  (:require [midje.sweet :refer :all]
            [othello.random-player.player :refer :all]))

(def no-moves-for-white-board {
  :width 8
  :height 8
  :max-index 63
  :squares [
    "-" "-" "-" "-" "-" "-" "-" "-"
    "-" "-" "-" "-" "-" "-" "-" "-"
    "-" "-" "-" "w" "w" "-" "-" "-"
    "-" "-" "w" "b" "b" "w" "-" "-"
    "-" "-" "w" "b" "b" "w" "-" "-"
    "-" "-" "-" "w" "w" "-" "-" "-"
    "-" "-" "-" "-" "-" "-" "-" "-"
    "-" "-" "-" "-" "-" "-" "-" "-"]})

(def one-move-for-white-board {
  :width 8
  :height 8
  :max-index 63
  :squares [
    "-" "-" "-" "-" "-" "-" "-" "-"
    "-" "-" "-" "-" "-" "-" "-" "-"
    "-" "-" "-" "-" "w" "-" "-" "-"
    "-" "-" "w" "b" "b" "w" "-" "-"
    "-" "-" "w" "b" "b" "w" "-" "-"
    "-" "-" "-" "w" "w" "-" "-" "-"
    "-" "-" "-" "-" "-" "-" "-" "-"
    "-" "-" "-" "-" "-" "-" "-" "-"]})

(fact "Calculate a move"
  (calculate-move no-moves-for-white-board :white) => -1
  (calculate-move one-move-for-white-board :white) => 19)