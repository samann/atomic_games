(ns othello.t-game
  (:require [midje.sweet :refer :all]
            [othello.game :refer :all]
            [othello.board :as board]
            [othello.strategies :as strategy]
            [othello.view :as view]))

(fact "validate-move checks for position and optional errors"
  (validate-move {} {}) => {:errors ["Failed to validate move schema"]}
  (validate-move {} {:errors ["Something"]}) => {:errors ["Failed to validate move schema"]}
  (validate-move {} {:position -1 :errors ["Something"]}) => {:position -1 :errors ["Something"]}

  (validate-move {:board ...board... :current-player :white} {:position 1})
    => {:position 1}
  (provided
    (board/valid-move? ...board... 1 :white) => true))

(fact "start-game initializes state and runs game"
  (start-game ...options...) => anything
  (provided
    (board/create ...options...) => ...board...
    (get-starting-player ...options...) => :black
    (view/create ...options... ...board...) => ...view...
    (load-strategy :black ...options...) => ...b-strategy...
    (load-strategy :white ...options...) => ...w-strategy...
    (load-strategy :remote-strategy ...options...) => ...r-strategy...
    (run-game {:moves 0
               :black-moves []
               :white-moves []
               :board-history [...board...]
               :board ...board...
               :current-player :black
               :view ...view...
               :black-strategy ...b-strategy...
               :white-strategy ...w-strategy...
               :remote-strategy ...r-strategy...
               :min-turn-time nil
               :max-turn-time nil}) => anything))

(fact "run-game initializes state and runs until game over."
  (let [state1 {:board ...board1... :current-player :black}
        state2 {:board ...board2... :current-player :white}]
    (run-game state1) => anything
    (provided
      (board/any-valid-moves? ...board1...) => true
      (board/any-valid-moves? ...board2...) => false
      (take-turn-and-wait state1) => {:state state2}
      (handle-game-over state2) => anything)))

(fact "run-game exits loop on player error."
  (let [state1 {:board ...board1... :current-player :black}
        state2 {:board ...board2... :current-player :white}]
    (run-game state1) => anything
    (provided
      (board/any-valid-moves? ...board1...) => true
      (take-turn-and-wait state1) => {:state state2 :errors ["Timed out!"]} :times 1
      (handle-player-error state2 ["Timed out!"]) => anything)))

(fact "take turn returns errors for a failed move"
  (take-turn ...state...)
    => {:state ...state... :errors ["Error!"]}
  (provided
    (get-next-move ...state...) => {:position -1 :errors ["Error!"]}))

(fact "take turn returns errors for an invalid move"
  (take-turn ...state...)
    => {:state ...state... :errors ["Invalid move!"]}
  (provided
    (get-next-move ...state...) => {:position 1}
    (validate-move ...state... {:position 1}) => {:errors ["Invalid move!"]}))

(fact "take turn updates board for valid move"
  (let [initial-state  {:moves 0
                        :black-moves []
                        :white-moves []
                        :board-history []
                        :board ...board1...
                        :current-player :black}
        intermediate-state {:moves 1
                        :black-moves [1]
                        :white-moves []
                        :board-history [...board1...]
                        :board ...board2...
                        :current-player :black}
        expected-state {:moves 1
                        :black-moves [1]
                        :white-moves []
                        :board-history [...board1...]
                        :board ...board2...
                        :black-score 5
                        :white-score 6
                        :previous-player :black
                        :current-player :white}]
    (take-turn initial-state) => {:state expected-state}
    (provided
      (get-next-move initial-state) => {:position 1}
      (validate-move initial-state {:position 1}) => {:position 1}
      (board/make-move ...board1... 1 :black) => ...board2...
      (board/score ...board2... :black) => 5
      (board/score ...board2... :white) => 6
      (get-next-player intermediate-state) => :white
      (view/display-move expected-state) => anything)))

(fact "get-starting-player return :black or given option"
  (get-starting-player {}) => :black
  (get-starting-player {:starting-player :black}) => :black
  (get-starting-player {:starting-player :white}) => :white)

(defn strategy1 [state]
  (println "strategy1 called" state))

(defn strategy2 [state]
  (println "strategy2 called"))

; (fact "get-next-move asks the appropriate strategy for the next move"
;   (get-next-move {:board ...board...
;                :current-player :white
;                :white-strategy {:fn strategy1}}) => ...move...
;   (provided
;     (strategy1 anything) => ...move...)

  ; Why don't the nested maps match in the provided block?
  ; (let [state {:board ...board...
  ;              :current-player :white
  ;              :white-strategy {:fn strategy1}}]
  ;   (get-next-move state) => ...move...
  ;   (provided
  ;     (strategy1 state) => ...move...))

  ; (let [state {:board ...board...
  ;              :current-player :black
  ;              :black-strategy {:fn strategy2}}]
  ;   (get-next-move state) => ...move...
  ;   (provided
  ;     (strategy2 state) => ...move...)))
; )

(fact "get-next-player returns opponent if opponent has moves"
  (get-next-player {:board ...board...
                    :current-player :black}) => :white
  (provided
    (board/opponent :black) => :white
    (board/any-valid-moves-for-player? ...board... :white) => true))

(fact "get-next-player returns current-player if opponent has no moves"
  (get-next-player {:board ...board...
                    :current-player :black}) => :black
  (provided
    (board/opponent :black) => :white
    (board/any-valid-moves-for-player? ...board... :white) => false))


