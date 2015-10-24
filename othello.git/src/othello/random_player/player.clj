(ns othello.random-player.player
  (:require [othello.board :refer :all]))

(defn calculate-move [board player]
  (if (any-valid-moves-for-player? board player)
    (let [moves (valid-moves-for-player board player)]
      (rand-nth moves))
    -1))