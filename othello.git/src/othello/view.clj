(ns othello.view
  (:require [othello.views.console :as console]
            [othello.views.gui :as gui]))

(defn create-view [type board]
  (case type
    :console console/interface
    :gui (gui/create board)))

(defn create [options board]
  (vec (map #(create-view % board) (:ui options))))

(defn display-initial-state [state]
  (doseq [view (:view state)]
    ((:display-initial-state view) view state)))

(defn display-move [state]
  (doseq [view (:view state)]
    ((:display-move view) view state)))

(defn display-game-over [state]
  (doseq [view (:view state)]
    ((:display-game-over view) view state)))

(defn display-player-error [state errors]
  (doseq [view (:view state)]
    ((:display-player-error view) view state errors)))

(defn get-move [state]
  (some (fn [view]
          (let [move ((:get-move view) view state)]
            (when (> move -1) move)))
    (:view state)))
