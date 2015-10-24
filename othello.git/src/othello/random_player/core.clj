(ns othello.random-player.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as string]
            [othello.util :as util]
            [othello.board :as board]
            [othello.random-player.player :as player])
  (:gen-class))

(def cli-options
  [["-b" "--board BOARD" "JSON reprresentation of the board."
    :validate [board/parse-board-string "Could not parse the given board."]]
   ["-p" "--player PLAYER" "The player to use (white or black)."
    :validate [board/parse-player-string "Invalid player symbol (must be white or black)."]]
   ["-t" "--time MILLIS" "The maximum time allowed for a move."]
   ["-h" "--help"]])

(defn invalid-options? [options]
  (not (util/contains-keys? options :board :player)))

(defn usage [options-summary]
  (->> ["This application is an AI for an Othello game. It picks a valid move at random."
        ""
        "Usage: -b BOARD -p PLAYER"
        "Where BOARD is a JSON reprresentation of the board and the player specifies \"white\" or \"black\""
        "Options:"
        options-summary
        ""]
       (string/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn invalid-options-msg [summary]
  (println "Sorry, some required options were missing.")
  (newline)
  (usage summary))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn -main
  "Main needs to:
    - Validate the given args.
    - Parse a board from the board JSON.
    - Invoke the player with the board to return a move."
  [& args]
  (let [{:keys [options errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) (exit 0 (usage summary))
      errors (exit 1 (error-msg errors))
      (invalid-options? options) (exit 1 (invalid-options-msg summary)))
    (System/exit (player/calculate-move (board/parse-board-string (:board options)) (board/parse-player-string (:player options))))))
