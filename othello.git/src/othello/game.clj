(ns othello.game
  (:require [othello.board :as board]
            [othello.view :as view]
            [othello.util :as util]
            [othello.strategies :as strategy]
            [clojure.tools.logging :as log]
            [cheshire.core :as json]
            [clojure.java.shell :as shell]
            [ring.adapter.jetty :as jetty]
            [clojure.pprint :refer :all]
            [schema.core :as s :refer [defschema]]))

(defn get-next-player
  "Answer which player should take the next turn given the current state."
  [state]
  (let [{:keys [board current-player]} state
        opponent (board/opponent current-player)]
    (if (board/any-valid-moves-for-player? board opponent)
      opponent
      current-player)))

(def remote-strategy (ref nil))

(defn web-ai-handler [request]
  (let [data     (json/parse-string (slurp (:body request)) true)
        state    {:board          (:board data)
                  :current-player (keyword (:current-player data))
                  :max-turn-time  (Integer/parseInt (:max-turn-time data))}
        strategy (:fn @remote-strategy)
        result   (strategy state)
        response (json/generate-string result)]

  {:status 200
   :headers {"Content-Type" "application/json"}
   :body response}))

(defn get-next-move
  "Determine the next move from the current player and strategy."
  [state]
  (let [strategy-key (if (= :black (:current-player state)) :black-strategy :white-strategy)
        strategy-data (strategy-key state)
        strategy-fn (:fn strategy-data)]
    (try
      (util/time-limited (:max-turn-time state)
        (strategy-fn state))
      (catch java.util.concurrent.TimeoutException e {:errors "Timed out!!!"}))))

(defn get-starting-player
  "Answer the starting player given the options."
  [options]
  (get options :starting-player :black))

(defn finalize-state [state]
  (let [board (:board state)]
    (-> state
      (assoc :winner (board/determine-winner board)))))

(defn handle-game-over [state]
  (let [final (finalize-state state)]
    (view/display-game-over final)
    final))

(defn handle-player-error [state errors]
  (let [final (finalize-state state)]
    (view/display-player-error final errors)
    final))


(defschema Move
  {:position s/Int
   (s/optional-key :errors) [s/Str]})

(defn validate-move [state move]
  (try
    (s/validate Move move)
    (cond
      (seq
        (:errors move)) move
      (board/valid-move? (:board state) (:position move) (:current-player state))
        move
      :else {:errors ["Invalid move!"]})
    (catch Exception e {:errors ["Failed to validate move schema"]})))

(defn take-turn [state]
  (let [{:keys [position errors]} (validate-move state (get-next-move state))]
    (if-not (empty? errors)
      {:state state :errors errors}
      (let [{:keys [moves
                    black-moves
                    white-moves
                    board-history
                    board
                    current-player]} state
            player-moves-key (if (= current-player :black) :black-moves :white-moves)
            player-moves (if (= current-player :black) black-moves white-moves)
            intermediate (-> state
                           (assoc :moves (inc moves))
                           (assoc player-moves-key (conj player-moves position))
                           (assoc :board-history (conj board-history board))
                           (assoc :board (board/make-move board position current-player)))
            final (-> intermediate
                    (assoc :black-score (board/score (:board intermediate) :black))
                    (assoc :white-score (board/score (:board intermediate) :white))
                    (assoc :previous-player current-player)
                    (assoc :current-player (get-next-player intermediate)))]
        (view/display-move final)
        {:state final}))))

(defn take-turn-and-wait [state]
  (util/exec-and-wait (:min-turn-time state) take-turn state))

(defn run-game [state]
  (view/display-initial-state state)
  (loop [state state]
    (if (board/any-valid-moves? (:board state))
      (let [{:keys [state errors]} (take-turn-and-wait state)]
        (if (empty? errors)
          (recur state)
          (handle-player-error state errors)))
      (handle-game-over state))))

(defn load-strategy [strat options]
  (strategy/get-strategy (strat options)))

(defn create-state [options]
  (let [board (board/create options)]
    {:moves 0
     :black-moves []
     :white-moves []
     :board-history [board]
     :board board
     :current-player (get-starting-player options)
     :view (view/create options board)
     :black-strategy (load-strategy :black options)
     :white-strategy (load-strategy :white options)
     :remote-strategy (load-strategy :remote-strategy options)
     :min-turn-time (:min-turn-time options)
     :max-turn-time (:max-turn-time options)}))



(defn start-game [options]
  (if (nil? (:remote-strategy options))
    (run-game (create-state options))
    ((dosync (ref-set remote-strategy (:remote-strategy (create-state options))))
     (jetty/run-jetty web-ai-handler {:port 3001}))))


