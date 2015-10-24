(ns acceptance.t-sample-games
  (:require [midje.sweet :refer :all]
            [othello.board :as board]
            [othello.game :refer :all]))

(defn next-move [moves state]
  (let [move (first @moves)]
    (swap! moves rest)
    {:position move}))

(defn playback-strategy [coll]
  (let [moves (atom coll)]
    {:fn (partial next-move moves)}))

(defn create-playback-state [black-moves white-moves]
  (let [board (board/create {})]
    {:moves 0
     :black-moves []
     :white-moves []
     :board-history [board]
     :board board
     :current-player (get-starting-player {})
     :black-strategy (playback-strategy black-moves)
     :white-strategy (playback-strategy white-moves)
     :min-turn-time 0
     :max-turn-time 7000}))

(def black-moves-1
  [26 45 10 18 42 24 43 54 62 46 9 1 49 51 16 58 8 63 60 55 40 22 11 13 3 48 56 31 6 5])
(def white-moves-1
  [34 19 37 20 25 33 53 44 17 50 2 0 41 59 47 61 38 57 52 21 39 12 30 29 4 23 14 32 7 15])
(def final-board-1 {
  :width 8
  :height 8
  :max-index 63
  :squares [
    "w" "w" "w" "w" "w" "b" "b" "w"
    "w" "b" "b" "w" "b" "b" "w" "w"
    "w" "b" "w" "b" "b" "w" "w" "w"
    "w" "w" "w" "b" "b" "w" "b" "b"
    "w" "w" "w" "w" "w" "w" "b" "b"
    "b" "b" "b" "w" "w" "b" "w" "b"
    "b" "b" "b" "b" "b" "b" "b" "b"
    "b" "b" "b" "b" "b" "b" "b" "b"]})

(fact "Check first sample game"
  (let [state (create-playback-state black-moves-1 white-moves-1)
        result (run-game state)]
  (:moves result) => 60
  (:white-score result) => 28
  (:black-score result) => 36
  (:winner result) => :black
  (:board result) => final-board-1))

(def black-moves-2
  [26 45 10])
(def white-moves-2
  [34 19 -1])
(def final-board-2 {
  :width 8
  :height 8
  :max-index 63
  :squares [
    "-" "-" "-" "-" "-" "-" "-" "-"
    "-" "-" "b" "-" "-" "-" "-" "-"
    "-" "-" "-" "b" "-" "-" "-" "-"
    "-" "-" "b" "w" "b" "-" "-" "-"
    "-" "-" "w" "w" "b" "-" "-" "-"
    "-" "-" "-" "-" "-" "b" "-" "-"
    "-" "-" "-" "-" "-" "-" "-" "-"
    "-" "-" "-" "-" "-" "-" "-" "-"]})

(fact "Check second sample game (white returns error position)"
  (let [state (create-playback-state black-moves-2 white-moves-2)
        result (run-game state)]
  (:moves result) => 5
  (:white-score result) => 3
  (:black-score result) => 6
  (:winner result) => :black
  (:board result) => final-board-2))

(def black-moves-3
  [26 45 10])
(def white-moves-3
  [34 19 63])
(def final-board-3 {
  :width 8
  :height 8
  :max-index 63
  :squares [
    "-" "-" "-" "-" "-" "-" "-" "-"
    "-" "-" "b" "-" "-" "-" "-" "-"
    "-" "-" "-" "b" "-" "-" "-" "-"
    "-" "-" "b" "w" "b" "-" "-" "-"
    "-" "-" "w" "w" "b" "-" "-" "-"
    "-" "-" "-" "-" "-" "b" "-" "-"
    "-" "-" "-" "-" "-" "-" "-" "-"
    "-" "-" "-" "-" "-" "-" "-" "-"]})

(fact "Check third sample game (white returns invalid play)"
  (let [state (create-playback-state black-moves-3 white-moves-3)
        result (run-game state)]
  (:moves result) => 5
  (:white-score result) => 3
  (:black-score result) => 6
  (:winner result) => :black
  (:board result) => final-board-3))
