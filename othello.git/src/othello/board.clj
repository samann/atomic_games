(ns othello.board
  (:require [cheshire.core :refer [generate-string parse-string parse-stream]]
            [clojure.walk :as walk]
            [schema.core :as s :refer [defschema]]
            [clojure.string :refer [upper-case]]))

(defschema Board
  {:width s/Int
   :height s/Int
   :max-index s/Int
   :squares [s/Str]})

(def standard-width 8)
(def standard-height 8)

(def black-symbol "b")
(def white-symbol "w")
(def empty-symbol "-")

(defn highlight-changes [old-board board]
  (let [diff-squares (vec (map #(if (= %1 %2) %2 (upper-case %2))
                            (:squares old-board)
                            (:squares board)))]
    (assoc board :squares diff-squares)))

(defn valid-dimensions?
  "Let's only allow squares for now."
  [w h]
  (and (< 0 w) (< 0 h) (= w h)))

(defn- valid-schema? [board]
  (try
    (s/validate Board board)
  (catch Exception e false)))

(defn serialize-board [board]
  (generate-string board))

(defn serialize-player [player]
  (name player))

(defn valid-board?
  [board]
  (and
    (valid-schema? board)
    (valid-dimensions? (:width board) (:height board))
    (= (inc (:max-index board)) (* (:width board) (:height board)))
    (= (inc (:max-index board)) (count (:squares board)))))

(defn valid-player?
  "Answer whether a valid player has been given."
  [player]
  (or (= player :white) (= player :black)))

(defn opponent [player]
  (if (= player :black) :white :black))

(defn player-symbol [player]
  (cond
    (= player :black) black-symbol
    (= player :white) white-symbol
    :else empty-symbol))

(defn player-name [player]
  (cond
    (= player :black) "Black"
    (= player :white) "White"
    :else "Unknown"))

(defn opponent-symbol [player]
  (player-symbol (opponent player)))

(defn score
  "Count the number of squares occupied by the given player"
  [board player]
  (if (valid-player? player)
    (count (filter #{(player-symbol player)} (:squares board)))))

(defn determine-winner
  "This function returns the winner given the current state of the board.
   It does not validate that the game is over."
  [board]
  (let [b-score (score board :black)
        w-score (score board :white)]
    (cond
      (> b-score w-score) :black
      (> w-score b-score) :white
      :else :tie)))

(defn get-piece [board position]
  (get (:squares board) position))

(defn place-piece [board position player]
  ; (println "Placing piece:" position "player" player)
  (assoc-in board [:squares position] (player-symbol player)))

(def directions {:up         (fn [width] (- width))
                 :down       (fn [width] width)
                 :left       (fn [width] -1)
                 :right      (fn [width] 1)
                 :up-left    (fn [width] (- (+ width 1)))
                 :up-right   (fn [width] (- (- width 1)))
                 :down-left  (fn [width] (- width 1))
                 :down-right (fn [width] (+ width 1))})

(defn all-directions [board]
  (let [width (:width board)]
    [((:up-left directions) width)
     ((:up directions) width)
     ((:up-right directions) width)
     ((:left directions) width)
     ((:right directions) width)
     ((:down-left directions) width)
     ((:down directions) width)
     ((:down-right directions) width)]))

(defn can-move-further [board position direction]
  (let [width (:width board)
        row (quot position width)
        max-row (dec (:height board))
        row-start (* row width)
        row-end (dec (+ row-start width))
        prev-start (- row-start width)
        prev-end (- row-end width)
        next-start (+ row-start width)
        next-end (+ row-end width)
        next-pos (+ position direction)]
    (cond
      (= direction ((:up directions) width))
        (> row 0)
      (= direction ((:down directions) width))
        (< row max-row)
      (= direction ((:left directions) width))
        (> position row-start)
      (= direction ((:right directions) width))
        (< position row-end)
      (= direction ((:up-left directions) width))
        (and (> row 0) (>= next-pos prev-start))
      (= direction ((:up-right directions) width))
        (and (> row 0) (<= next-pos prev-end))
      (= direction ((:down-left directions) width))
        (and (< row max-row) (>= next-pos next-start))
      (= direction ((:down-right directions) width))
        (and (< row max-row) (<= next-pos next-end))
      :else false)))

(defn find-bracketing-piece
   "Return the index of the bracketing piece, nil if there isn't one."
   [board position player direction]
   (cond
     (= (get-piece board position) (player-symbol player))
       position
     (and (= (get-piece board position) (opponent-symbol player))
          (can-move-further board position direction))
        (recur board (+ position direction) player direction)
     :else nil))

(defn would-flip?
  "If this move would result in any flips, return the square of the bracketing piece."
  [board position player direction]
  (if (can-move-further board position direction)
    (let [position (+ position direction)]
      (boolean
         (and (= (get-piece board position) (opponent-symbol player))
           (find-bracketing-piece board position player direction))))))

(defn within-board? [board position]
  (let [max-index (:max-index board)]
    (and (>= position 0) (<= position max-index))))

(defn valid-move? [board position player]
  (boolean
    (and (within-board? board position)
      (= (get-piece board position) empty-symbol)
      (some (partial would-flip? board position player) (all-directions board)))))

(defn valid-moves-for-player [board player]
  (filter #(valid-move? board % player) (range 64)) )

(defn any-valid-moves-for-player? [board player]
  (boolean (seq (valid-moves-for-player board player))))

(defn any-valid-moves? [board]
  (or (any-valid-moves-for-player? board :white)
      (any-valid-moves-for-player? board :black)))

(defn starting-position [board]
  (let [width (:width board)
        height (:height board)]
    (+ (dec (/ width 2)) (* width (dec (/ height 2))))))

(defn make-flips
   "Make any flips available in the given direction."
   [board position player direction]
   (let [needs-flips (would-flip? board position player direction)]
     (if-not needs-flips
       board
       (if-let [bracketer (find-bracketing-piece board (+ direction position) player direction)]
         (loop [board board
                position (+ position direction)]
           (if (= position bracketer)
             board
             (recur (place-piece board position player) (+ position direction))))
         (throw Exception "Something bad happened")))))

(defn update-board-for-move
   "Update board to reflect move by a player"
   [board position player]
   (let [all-dir (all-directions board)]
     (loop [board (place-piece board position player)
            idx 0]
       (if (>= idx (count directions))
          board
          (recur (make-flips board position player (get all-dir idx)) (inc idx))))))

(defn make-move [board position player]
  (if (valid-move? board position player)
    (update-board-for-move board position player)
    board))

(defn move-up [board position]
  (+ position ((:up directions) (:width board))))

(defn move-right [board position]
  (+ position ((:right directions) (:width board))))

(defn move-left [board position]
  (+ position ((:left directions) (:width board))))

(defn move-down [board position]
  (+ position ((:down directions) (:width board))))

(defn intialize [board]
  (let [position (starting-position board)
        board (place-piece board position :white)
        position (move-right board position)
        board (place-piece board position :black)
        position (move-down board position)
        board (place-piece board position :white)
        position (move-left board position)
        board (place-piece board position :black)]
    board))

(defn create
  [options]
  (let [w (get options :width standard-width)
        h (get options :height standard-height)]
    (if (valid-dimensions? w h)
      (intialize {:width w
                  :height h
                  :max-index (dec (* w h))
                  :squares (vec (repeat (* w h) empty-symbol))})
      (throw (IllegalArgumentException.
                (str "The given board width: " w " and height: " h " are not valid."))))))

(defn parse-board [value parse]
  (try
    (let [raw (parse value)
          board (walk/keywordize-keys raw)]
      (when (valid-board? board)
        board))
  (catch Exception e nil)))

(defn parse-board-string [value]
  (parse-board value parse-string))

(defn parse-board-stream [value]
  (parse-board value parse-stream))

(defn parse-player-string [value]
  (let [player (keyword value)]
    (when (valid-player? player)
      player)))

