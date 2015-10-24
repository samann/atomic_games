(ns othello.views.gui
  (:use [seesaw core color graphics behave mig])
  (:require [clojure.string :refer [join upper-case lower-case]]
            [othello.board :as board]
            [clojure.core.async :refer [put! chan <!!]]))

(def board-state (atom nil))
(def pending-moves (chan))

(def background (color 51 204 51 255))
(def highlighted-background (color 102 255 102 255))

(defn highlighted? [value]
  (re-seq #"[BWP]" value))

(defn render [c g]
  (let [board @board-state
        w (.getWidth c)
        h (.getHeight c)
        w (min w h)
        h (min w h)]
    (doto g
      (.setColor background)
      (.fillRect 0 0 w h))
    (when board
      (let [rows (:height board)
            columns (:width board)
            squares (:squares board)
            col-w (/ w columns)
            row-h (/ h rows)]
        (.setColor g java.awt.Color/BLACK)
        (doseq [c (range (inc columns))]
          (.drawLine g (* c col-w) 0 (* c col-w) h))
        (doseq [r (range (inc rows))]
          (.drawLine g 0 (* r row-h) w (* r row-h)))
        (doseq [c (range columns)
                r (range rows)]
          (let [value (get squares (+ c (* r columns)))
                x  (* c col-w)
                y  (* r row-h)
                ox (+ 10 x)
                oy (+ 10 y)
                ow (- col-w 20)
                oh (- row-h 20)
                oc (case (lower-case value)
                        "b" java.awt.Color/BLACK
                        "w" java.awt.Color/WHITE
                        (color 0 0 0 0))]
            (when (highlighted? value)
              (.setColor g highlighted-background)
              (.fillRect g (inc x) (inc y) (dec col-w) (dec row-h)))
            (.setColor g oc)
            (.fillOval g ox oy ow oh)))))))

(defn canvas-clicked [e]
  (let [board @board-state
        columns (:width board)
        rows (:height board)
        squares (:squares board)
        w (.getWidth (.getSource e))
        h (.getHeight (.getSource e))
        col-w (/ w columns)
        row-h (/ h rows)
        col (quot (.getX e) col-w)
        row (quot (.getY e) row-h)
        i   (+ col (* row columns))]
    (when (= (get squares i) "P")
      (put! pending-moves i))))

(defn make-ui [board]
  (let [w (* 75 (:width board))
        h (* 75 (:height board))]
    (native!)
    (frame
      :title "Othello"
      :on-close :exit
      :content
        (border-panel
          :border 5
          :north
            (mig-panel
              :constraints ["wrap 2"
                            "[]20px[]"
                            "[]5px[]5px[]5px[]25px[]"]
              :items [ ["Moves:"          ] [(label :id :moves)]
                       ["Current player:" ] [(label :id :current-player)]
                       ["White score:"    ] [(label :id :white-score)]
                       ["Black score:"    ] [(label :id :black-score)]
                       [(label :id :summary
                               :font {:style :bold :size 20})
                         "span, grow"]])
          :center
            (canvas :id :canvas
                    :paint render
                    :preferred-size [w :by h]
                    :listen [:mouse-clicked canvas-clicked])))))

(defn update-labels [view state]
  (let [frame (:frame view)]
    (text! (select frame [:#moves]) (:moves state))
    (text! (select frame [:#current-player]) (board/player-name (:current-player state)))
    (text! (select frame [:#white-score]) (:white-score state))
    (text! (select frame [:#black-score]) (:black-score state))))

(defn update-board [view state]
  (let [old-board (last (:board-history state))
      board (:board state)
      highlighted (board/highlight-changes old-board board)]
    (reset! board-state highlighted))
  (update-labels view state)
  (.repaint (:frame view)))

(defn create-and-show [board]
  (-> (make-ui board) pack! show!))

(defn display-initial-state [view state]
  (update-board view state))

(defn display-move [view state]
  (update-board view state))

(defn game-over-message [state]
  (str "Game over! "
    (let [winner (:winner state)]
      (if (= :tie winner)
        "It's a tie! Celebrate your shared victory!"
        (let [winner-name (board/player-name winner)]
          (str "Congratulations " winner-name "! You win!"))))))

(defn player-error-message [state]
  (let [current-player (:current-player state)
        loser (board/player-name current-player)
        winner (board/player-name (board/opponent current-player))]
    (str
      "Oh no, " loser " failed to provide a valid move! "
      "Congratulations " winner "! You win!!!")))

(defn display-game-over [view state]
  (update-labels view state)
  (update-board view state)
  (text! (select (:frame view) [:#summary]) (game-over-message state)))

(defn display-player-error [view state errors]
  (update-labels view state)
  (update-board view state)
  (text! (select (:frame view) [:#summary]) (player-error-message state)))

(defn show-message [view msg]
  ; (text! (select (:frame view) [:#summary]) msg)
  )

(defn clear-messages [view]
  ; (text! (select (:frame view) [:#summary]) "")
  )

(defn highlight-valid-moves [board valid-moves]
  (reduce (fn [b m]
            (assoc-in b [:squares m] "P"))
          board
          valid-moves))

(defn indicate-waiting-for-move [view state]
  (let [board (:board state)
        player (:current-player state)
        valid-moves (board/valid-moves-for-player board player)
        highlighted (highlight-valid-moves board valid-moves)]
    (reset! board-state highlighted)
    (.repaint (:frame view))
    (show-message view "Your move!")))

(defn get-move [view state]
    (indicate-waiting-for-move view state)
    (let [move (<!! pending-moves)]
      (clear-messages view)
      move))

(defn create [board]
  { :frame (create-and-show board)
    :display-initial-state display-initial-state
    :display-move display-move
    :display-game-over display-game-over
    :display-player-error display-player-error
    :get-move get-move})
