(ns othello.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as string]
            [clojure.set :as s]
            [othello.util :as util]
            [othello.game :as game])
  (:gen-class))

(defn valid-strategy? [strategy]
  (or (some #{strategy} ["interactive" "random"])
    (re-matches #"http://.*" strategy)
    (util/file-exists? strategy)))

(defn valid-remote-strategy? [strategy]
  (util/file-exists? strategy))

(defn parse-ui-arg [arg]
  (keyword (string/trim arg)))

(defn valid-ui-arg? [arg]
  (some #{arg} #{:console :gui}))

(defn assoc-ui-arg [previous key val]
  (let [oldval (get previous key)]
    (assoc previous key
      (if (set? oldval)
        (merge oldval val)
        (hash-set val)))))

(defn valid-timeout [arg]
  (and (integer? arg) (pos? arg)))

(def cli-options
  [["-b" "--black EXE" "AI strategy for the black player"
    :validate [valid-strategy? "Could not locate the given strategy."]]
   ["-w" "--white EXE" "AI strategy for the white player"
    :validate [valid-strategy? "Could not locate the given strategy."]]
   ["-r" "--remote-strategy EXE" "AI strategy to be accessed by game server"
    :validate [valid-remote-strategy? "Could not locate the given strategy."]]
   ["-u" "--ui TYPE" "User interface type, one of [console, gui]"
    :default #{:gui}
    :parse-fn parse-ui-arg
    :assoc-fn assoc-ui-arg
    :validate [valid-ui-arg? "Must be one of [console, gui]"]]
   ["-m" "--min-turn-time MILLIS" "Minimum amount of time to wait between turns."
    :default 1500
    :parse-fn #(Integer/parseInt %)
    :validate [valid-timeout "Must be a positive integer."]]
   ["-x" "--max-turn-time MILLIS" "Maximum amount of time to allow an AI for a turn."
    :default 7000
    :parse-fn #(Integer/parseInt %)
    :validate [valid-timeout "Must be a positive integer."]]
   ["-h" "--help"]])

(defn invalid-options? [options]
  (not (or (util/contains-keys? options :remote-strategy) 
           (util/contains-keys? options :black :white))))

(defn usage [options-summary]
  (->> ["This application is a board for dueling Othello AI implementations."
        ""
        "You must specify a strategy for the black and white players."
        ""
        "The strategy can be one of three types:"
        "interactive - the user will interact with the game to make moves"
        "random - the game will make a random valid move for the player"
        "[EXE] - the game will invoke an executable to determine the next move"
        ""
        "By default, a gui will launch to render the game. You can specify either"
        "to use a console UI (-u console) a graphical ui (-u gui) or"
        "both (-u gui -u console)."
        ""
        "The game will by default leave 1.5 seconds between turns so that the audience"
        "can follow along. You can change this with the -m arg (-m 2000 for 2 seconds)."
        ""
        "Usage:"
        "lein run -w EXE -b EXE"
        "lein run -w random -b random -g console"
        "lein run -w random -b interactive -m 3000"
        ""
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
    - Validate the given args, and store the AI players.
    - Setup the initial game state and display the UI.
    - Start processing moves, until the game is finished."
  [& args]
  (let [{:keys [options errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) (exit 0 (usage summary))
      errors (exit 1 (error-msg errors))
      (invalid-options? options) (exit 1 (invalid-options-msg summary)))
    (game/start-game options)))
