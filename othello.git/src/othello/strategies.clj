(ns othello.strategies
  (:require [othello.util :as util]
            [othello.board :as board]
            [clojure.tools.logging :as log]
            [org.httpkit.client :as http]
            [cheshire.core :as json]
            [clojure.java.shell :as shell]
            [clojure.pprint :refer :all]
            [othello.view :as view]
            [othello.random-player.player :as player]))

(defn execute-ai [executable state]
  (log/debug "Executing" executable "with args" state)
  (let [board-string (board/serialize-board (:board state))
              player-string (board/serialize-player (:current-player state))
              time-string (str (:max-turn-time state))
              output (shell/sh executable "-b" board-string "-p" player-string "-t" time-string)]
          (log/debug "Got result:" output)
          {:position (:exit output)}))

(defn execute-ai-fn [executable]
  (partial execute-ai executable))

(defn remote-ai [url state]
  (log/debug "Posting to" url "with args" state)
  (let [data {:board          (:board state)
              :current-player (:current-player state)
              :max-turn-time  (str (:max-turn-time state))}
        resp @(http/post url {:body (json/generate-string data) 
                              :headers {"Content-Type" "application/json"}})
        result (json/parse-string (:body resp))]
    (json/parse-string (:body resp) true)))

(defn remote-ai-fn [url]
  (partial remote-ai url))

(defn call-player-directly [state]
  {:position (player/calculate-move (:board state) (:current-player state))})

(defn get-interactive-move [state]
  (try
    {:position (view/get-move state)}
    (catch Exception e {:errors "Invalid move!!!"})))

(defn get-strategy 
  "Answer the strategy we should be using for the player given the options."
  [strategy]
  (case strategy
    nil {:fn nil}
    "random" {:fn call-player-directly}
    "interactive" {:fn get-interactive-move}
    (if (re-matches #"^http://.*" strategy)
      {:fn (remote-ai-fn strategy)}
      {:fn (execute-ai-fn strategy)})))

