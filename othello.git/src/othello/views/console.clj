(ns othello.views.console
  (:require [clojure.string :refer [join upper-case]]
            [othello.board :as board]))

(defn create [options board])

(defn print-divider []
  (println "----------"))

(defn print-board [board]
  (let [height (:height board)
        squares (:squares board)
        rows (partition height squares)]
    (doall (map #(println (join " " %)) rows))))


(defn display-scores [state]
  (print-divider)
  (println "Score")
  (println "Black:" (:black-score state))
  (println "White:" (:white-score state))
  (print-divider))

(defn display-player-moves [state]
  (println)
  (print-divider)
  (println "Black moves")
  (println (:black-moves state))
  (println "White moves")
  (println (:white-moves state))
  (print-divider)
  (println))

(defn display-initial-state [view state]
  (let [board (:board state)]
    (println "Starting game!!!")
    (print-divider)
    (print-board board)))

(defn display-move [view state]
  (let [old-board (last (:board-history state))
        board (:board state)]
    (dotimes [_ 3] (println))
    (println "Move" (:moves state) "-" (board/player-name (:previous-player state)))
    (display-scores state)
    (print-board (board/highlight-changes old-board board))))

(defn display-game-over [view state]
  (let [board (:board state)]
    (dotimes [_ 3] (println))
    (println "Game over!!!")
    (println "Total moves" (:moves state))
    (display-scores state)
    (print-board board)
    (display-player-moves state)
    (let [winner (:winner state)]
      (if (= :tie winner)
        (println "It's a tie! Celebrate your shared victory!")
        (let [winner-name (board/player-name winner)]
          (println (str "Congratulations " winner-name "! You win!!!")))))))

(defn display-player-error [view state errors]
  (let [current-player (:current-player state)
        loser (board/player-name current-player)
        winner (board/player-name (board/opponent current-player))]
    (println "Oh no, " loser " failed to provide a valid move:")
    (map #(println %) errors)
    (display-scores state)
    (print-board (:board state))
    (display-player-moves state)
    (println (str "Congratulations " winner "! You win!!!"))))

(defn get-move [view state]
  (let [board (:board state)
        player (:current-player state)
        valid-moves (board/valid-moves-for-player board player)]
    (println)
    (println (str "Please enter your move (valid moves are "
                  (join " " valid-moves)
                  "):")))
  (Integer/parseInt (read-line)))

(def interface {
  :create create
  :display-initial-state display-initial-state
  :display-move display-move
  :display-game-over display-game-over
  :display-player-error display-player-error
  :get-move get-move})
