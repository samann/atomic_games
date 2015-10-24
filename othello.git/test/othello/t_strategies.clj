(ns othello.t-game
  (:require [midje.sweet :refer :all]
            [othello.strategies :refer :all]))

(fact "get strategy returns appropriate strategy"
  (get-strategy "Blorg")
    => {:fn ...exec-fn...}
  (provided
    (execute-ai-fn "Blorg") => ...exec-fn...))

(fact "execute-ai-fn wraps execute-ai correctly"
  (let [exec-fn (execute-ai-fn "./test/player1.rb")]
    (exec-fn {}) => {:exit 10 :out "" :err ""}))
