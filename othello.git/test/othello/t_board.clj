(ns othello.t-board
  (:require [midje.sweet :refer :all]
            [othello.board :refer :all]
            [cheshire.core :refer [generate-string]]))

(def starting-board {
  :width 8
  :height 8
  :max-index 63
  :squares [
    "-" "-" "-" "-" "-" "-" "-" "-" ;  7
    "-" "-" "-" "-" "-" "-" "-" "-" ; 15
    "-" "-" "-" "-" "-" "-" "-" "-" ; 23
    "-" "-" "-" "w" "b" "-" "-" "-" ; 31
    "-" "-" "-" "b" "w" "-" "-" "-" ; 39
    "-" "-" "-" "-" "-" "-" "-" "-" ; 47
    "-" "-" "-" "-" "-" "-" "-" "-" ; 55
    "-" "-" "-" "-" "-" "-" "-" "-"]})

(def all-black-board {
  :width 8
  :height 8
  :max-index 63
  :squares [
    "-" "-" "-" "-" "-" "-" "-" "-"
    "-" "-" "-" "-" "-" "-" "-" "-"
    "-" "-" "-" "b" "b" "-" "-" "-"
    "-" "-" "b" "b" "b" "b" "-" "-"
    "-" "-" "b" "b" "b" "b" "-" "-"
    "-" "-" "-" "b" "b" "-" "-" "-"
    "-" "-" "-" "-" "-" "-" "-" "-"
    "-" "-" "-" "-" "-" "-" "-" "-"]})

(def no-moves-for-black-board {
  :width 8
  :height 8
  :max-index 63
  :squares [
    "-" "-" "-" "-" "-" "-" "-" "-"
    "-" "-" "-" "-" "-" "-" "-" "-"
    "-" "-" "-" "b" "b" "-" "-" "-"
    "-" "-" "b" "w" "w" "b" "-" "-"
    "-" "-" "b" "w" "w" "b" "-" "-"
    "-" "-" "-" "b" "b" "-" "-" "-"
    "-" "-" "-" "-" "-" "-" "-" "-"
    "-" "-" "-" "-" "-" "-" "-" "-"]})

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

(fact "create returns expected board"
  (create {}) => starting-board)

(fact "serialize-player returns a string"
  (serialize-player :white) => "white")

(fact "valid-dimensions? only allows squares for now"
  (valid-dimensions? 0 0) => false
  (valid-dimensions? -8 -8) => false
  (valid-dimensions? 8 8) => true
  (valid-dimensions? 16 16) => true)

(fact "opponent works"
  (opponent :black) => :white
  (opponent :white) => :black)

(fact "valid-player? does the right thing"
  (valid-player? :white) => true
  (valid-player? :black) => true
  (valid-player? :something) => false)

(fact "valid-board? does the right thing"
  (valid-board? {}) => false
  (valid-board? {:width 2 :height 4 :max-index 8 :squares []}) => false
  (valid-board? {:width 2 :height 2 :max-index 8 :squares ["w" "w" "w" "w"]}) => false
  (valid-board? {:width 2 :height 2 :max-index 3 :squares ["b" "w" "w" "w" "w"]}) => false
  (valid-board? {:width 2 :height 2 :max-index 3 :squares ["w" "w" "w" "w"]}) => true)

(fact "score computes the correct score for each player"
  (score no-moves-for-white-board :white) => 8
  (score no-moves-for-white-board :black) => 4
  (score no-moves-for-white-board :gray) => nil)

(fact "determine-winner returns player with higher score"
  (determine-winner ...state...) => :black
  (provided
    (score ...state... :white) => 10
    (score ...state... :black) => 11))

(fact "determine-winner returns tie if player scores are tied"
  (determine-winner ...state...) => :tie
  (provided
    (score ...state... :white) => 10
    (score ...state... :black) => 10))

(fact "within-board answers correctly"
  (within-board? starting-board -1) => false
  (within-board? starting-board 64) => false
  (within-board? starting-board 0) => true
  (within-board? starting-board 63) => true)

(fact "get-piece returns the expected piece"
  (get-piece starting-board 27) => white-symbol
  (get-piece starting-board 28) => black-symbol
  (get-piece starting-board 29) => empty-symbol)

(fact "all-directions provides the correct set of offsets"
  (all-directions {:width 8}) => [-9 -8 -7 -1 1 7 8 9]
  (all-directions {:width 10}) => [-11 -10 -9 -1 1 9 10 11])

(fact "would-flip answers correctly"
  (would-flip? starting-board 20 :white -9) => false
  (would-flip? starting-board 20 :white -8) => false
  (would-flip? starting-board 20 :white -7) => false
  (would-flip? starting-board 20 :white -1) => false
  (would-flip? starting-board 20 :white 1) => false
  (would-flip? starting-board 20 :white 7) => false
  (would-flip? starting-board 20 :white 8) => true
  (would-flip? starting-board 20 :white 9) => false)

(fact "valid-move? answers correctly"
  (valid-move? starting-board 19 :white) => false ; no-flip
  (valid-move? starting-board 20 :white) => true ; flip
  (valid-move? starting-board 26 :white) => false ; no-flip
  (valid-move? starting-board 27 :white) => false ; taken
  (valid-move? starting-board 28 :white) => false ; taken
  (valid-move? starting-board 29 :white) => true ; flip
  (valid-move? starting-board 43 :black) => false ; taken
  (valid-move? starting-board 44 :black) => true ; flip
  (valid-move? no-moves-for-white-board 21 :black) => false ; no-flip
  (valid-move? no-moves-for-white-board 22 :black) => true) ; flip

(fact "valid-moves-for-player returns the correct set of positions"
  (valid-moves-for-player starting-board :white) => [20 29 34 43]
  (valid-moves-for-player starting-board :black) => [19 26 37 44])

(fact "any-valid-moves-for-player? answers correctly"
  (any-valid-moves-for-player? starting-board :white) => true
  (any-valid-moves-for-player? starting-board :black) => true
  (any-valid-moves-for-player? all-black-board :white) => false
  (any-valid-moves-for-player? all-black-board :black) => false
  (any-valid-moves-for-player? no-moves-for-black-board :white) => true
  (any-valid-moves-for-player? no-moves-for-black-board :black) => false
  (any-valid-moves-for-player? no-moves-for-white-board :white) => false
  (any-valid-moves-for-player? no-moves-for-white-board :black) => true)

(fact "any-valid-moves-for-player? answers correctly"
  (any-valid-moves? starting-board) => true
  (any-valid-moves? all-black-board) => false
  (any-valid-moves? no-moves-for-black-board) => true
  (any-valid-moves? no-moves-for-white-board) => true)

(fact "starting position returns the correct square"
  (starting-position starting-board) => 27
  (starting-position {:width 10 :height 20}) => 94)


(fact "find-bracketing-piece answers correctly for starting-board"
  (find-bracketing-piece starting-board 28 :white -9) => nil
  (find-bracketing-piece starting-board 28 :white -8) => nil
  (find-bracketing-piece starting-board 28 :white -7) => nil
  (find-bracketing-piece starting-board 28 :white -1) => 27
  (find-bracketing-piece starting-board 28 :white 1) => nil
  (find-bracketing-piece starting-board 28 :white 7) => nil
  (find-bracketing-piece starting-board 28 :white 8) => 36
  (find-bracketing-piece starting-board 28 :white 9) => nil)

(def bracket-board-1 {
  :width 4
  :height 4
  :max-index 15
  :squares [
    "b" "-" "-" "w"
    "b" "b" "b" "-"
    "-" "w" "w" "b"
    "b" "w" "-" "-"]})

(fact "find-bracketing-piece reports the correct values for edge conditions"
  (find-bracketing-piece bracket-board-1 6 :white -1) => nil
  (find-bracketing-piece bracket-board-1 9 :black 1) => 11
  (find-bracketing-piece bracket-board-1 5 :white 4) => 9
  (find-bracketing-piece bracket-board-1 9 :black 4) => nil
  (find-bracketing-piece bracket-board-1 5 :white -4) => nil
  (find-bracketing-piece bracket-board-1 13 :black -1) => 12)

(fact "can-move-further does respects row and column boundaries"
  ; Top left
  (can-move-further bracket-board-1 0 -4) => false ; up
  (can-move-further bracket-board-1 0 4)  => true  ; down
  (can-move-further bracket-board-1 0 -1) => false ; left
  (can-move-further bracket-board-1 0 1)  => true  ; right
  (can-move-further bracket-board-1 0 -5) => false ; up-left
  (can-move-further bracket-board-1 0 -3) => false ; up-right
  (can-move-further bracket-board-1 0 3)  => false ; down-left
  (can-move-further bracket-board-1 0 5)  => true ; down-right

  ; Top right
  (can-move-further bracket-board-1 3 -4) => false ; up
  (can-move-further bracket-board-1 3 4)  => true  ; down
  (can-move-further bracket-board-1 3 -1) => true ; left
  (can-move-further bracket-board-1 3 1)  => false  ; right
  (can-move-further bracket-board-1 3 -5) => false ; up-left
  (can-move-further bracket-board-1 3 -3) => false ; up-right
  (can-move-further bracket-board-1 3 3)  => true ; down-left
  (can-move-further bracket-board-1 3 5)  => false ; down-right

  ; Bottom left
  (can-move-further bracket-board-1 12 -4) => true ; up
  (can-move-further bracket-board-1 12 4)  => false  ; down
  (can-move-further bracket-board-1 12 -1) => false ; left
  (can-move-further bracket-board-1 12 1)  => true  ; right
  (can-move-further bracket-board-1 12 -5) => false ; up-left
  (can-move-further bracket-board-1 12 -3) => true ; up-right
  (can-move-further bracket-board-1 12 3)  => false ; down-left
  (can-move-further bracket-board-1 12 5)  => false ; down-right

  ; Bottom right
  (can-move-further bracket-board-1 15 -4) => true ; up
  (can-move-further bracket-board-1 15 4)  => false  ; down
  (can-move-further bracket-board-1 15 -1) => true ; left
  (can-move-further bracket-board-1 15 1)  => false  ; right
  (can-move-further bracket-board-1 15 -5) => true ; up-left
  (can-move-further bracket-board-1 15 -3) => false ; up-right
  (can-move-further bracket-board-1 15 3)  => false ; down-left
  (can-move-further bracket-board-1 15 5)  => false ; down-right

  ; Center left
  (can-move-further bracket-board-1 8 -4) => true ; up
  (can-move-further bracket-board-1 8 4)  => true  ; down
  (can-move-further bracket-board-1 8 -1) => false ; left
  (can-move-further bracket-board-1 8 1)  => true  ; right
  (can-move-further bracket-board-1 8 -5) => false ; up-left
  (can-move-further bracket-board-1 8 -3) => true ; up-right
  (can-move-further bracket-board-1 8 3)  => false ; down-left
  (can-move-further bracket-board-1 8 5)  => true ; down-right

  ; Center right
  (can-move-further bracket-board-1 11 -4) => true ; up
  (can-move-further bracket-board-1 11 4)  => true  ; down
  (can-move-further bracket-board-1 11 -1) => true ; left
  (can-move-further bracket-board-1 11 1)  => false  ; right
  (can-move-further bracket-board-1 11 -5) => true ; up-left
  (can-move-further bracket-board-1 11 -3) => false ; up-right
  (can-move-further bracket-board-1 11 3)  => true ; down-left
  (can-move-further bracket-board-1 11 5)  => false ; down-right


  ; Center
  (can-move-further bracket-board-1 5 -4) => true ; up
  (can-move-further bracket-board-1 5 4)  => true  ; down
  (can-move-further bracket-board-1 5 -1) => true ; left
  (can-move-further bracket-board-1 5 1)  => true  ; right
  (can-move-further bracket-board-1 5 -5) => true ; up-left
  (can-move-further bracket-board-1 5 -3) => true ; up-right
  (can-move-further bracket-board-1 5 3)  => true ; down-left
  (can-move-further bracket-board-1 5 5)  => true) ; down-right

(def flip-board-1 {
  :width 4
  :height 4
  :max-index 15
  :squares [
    "-" "-" "w" "-"
    "b" "b" "w" "-"
    "-" "-" "-" "-"
    "b" "w" "-" "-"]})

(fact "would-flip? respects row and column boundaries"
  (would-flip? flip-board-1 1 :white 1) => false)


(def small-board-1 {
  :width 4
  :height 4
  :max-index 15
  :squares [
    "-" "-" "-" "-"
    "w" "b" "b" "-"
    "-" "w" "w" "b"
    "-" "-" "-" "-"]})

(def small-board-2 {
  :width 4
  :height 4
  :max-index 15
  :squares [
    "-" "-" "-" "-"
    "w" "w" "w" "w"
    "-" "w" "w" "b"
    "-" "-" "-" "-"]})

(def small-board-3 {
  :width 4
  :height 4
  :max-index 15
  :squares [
    "-" "-" "-" "-"
    "w" "w" "w" "w"
    "b" "b" "b" "b"
    "-" "-" "-" "-"]})

(fact "make-move updates the board correctly"
  (make-move small-board-1 8 :white) => small-board-1 ; no change, invalid move
  (find-bracketing-piece small-board-1 6 :white -1) => 4
  (make-move small-board-1 7 :white) => small-board-2
  (make-move small-board-2 8 :black) => small-board-3)

(fact "parse-player-string does the right thing"
  (parse-player-string "white") => :white
  (parse-player-string "black") => :black
  (parse-player-string nil) => nil
  (parse-player-string "something") => nil)

(fact "parse-board-string does the right thing"
  (parse-board-string nil) => nil
  (parse-board-string "something") => nil
  (parse-board-string "{invalid}") => nil
  (parse-board-string "{\"width\": 8}") => nil
  (parse-board-string (generate-string starting-board)) => starting-board
  (parse-board-string (generate-string small-board-1)) => small-board-1)

(fact "player-name does the right thing"
  (player-name :white) => "White"
  (player-name :black) => "Black"
  (player-name :some-guy) => "Unknown")

(def random-1 {
  :width 8
  :height 8
  :max-index 63
  :squares [
    "-" "-" "-" "-" "w" "w" "-" "-"
    "-" "-" "b" "b" "b" "w" "b" "-"
    "-" "-" "-" "b" "b" "b" "w" "-"
    "-" "-" "w" "w" "w" "w" "w" "w"
    "-" "w" "-" "w" "w" "b" "b" "-"
    "-" "-" "w" "w" "b" "b" "-" "-"
    "-" "w" "-" "-" "b" "b" "w" "-"
    "w" "-" "-" "-" "-" "-" "-" "w"
  ]})


(fact "square 24 is not valid for black"
  (valid-move? random-1 16 :black) => false
  (valid-move? random-1 24 :black) => false)
