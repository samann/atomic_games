# Othello

This application provides a playing field for competing Othello AI implementations. It accepts options to run two AIs against each other, or to play against an AI in an interactive mode. The application can provide a command line interface or a GUI.

## Installation

To run this application, you'll need to have leiningen installed:

http://leiningen.org/

Then simply navigate to the root of the repository and execute "lein run -- -h" to view options.

## Options
  -b, --black EXE                      AI strategy for the black player
  -w, --white EXE                      AI strategy for the white player
  -u, --ui TYPE               gui      User interface type, one of [console, gui]
  -m, --min-turn-time MILLIS  1500     Minimum amount of time to wait between turns.
  -x, --max-turn-time MILLIS  7000     Maximum amount of time to allow an AI for a turn.
  -h, --help

## Examples

Run two AIs against each other, using a GUI:
lein run -w ./ai1.sh -b ./ai2.sh

Run two AIs against each other, using the console, with no delay between turns:
lein run -w ./ai1.sh -b ./ai2.sh -u console -m 0

Play in interactive mode against an AI:
lein run -w ./ai1.sh -b interactive

## Implementing an AI
The application manages the state of the game, tracks statistics, and computes victory conditions. When the game determines that it needs a move for a given player, it will invoke the strategy specified for that player. To provide an AI for a player, pass an executable as the player strategy. The game will invoke this executable each time it determines that it's that player's turn. The executable will be passed information about the board state, how much time it is allowed to use when determining a move, and which player it represents. The executable is expected to return its move as the return code when it exits. An AI must return a valid move within the given time or it will forfeit the game.

The board state is passed as a JSON string after the -b label. The player is passed as either "black" or "white" after the -p label. Time allowed is passed after the -t label and is specified in milliseconds. A typical invocation might look like:

ai_exe.sh -b "...board json..." -p "black" -t 7000

Where the board JSON structure will look like:
{
  "width": 8,
  "height": 8,
  "max-index": 63,
  "squares": ["-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","w","b","-","-","-","-","-","-","b","w","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-","-"]
}

The "squares" array is a 1D array of the board squares. It will contain a "-" for an empty square, a "w" for a square occupied by the white player, and a "b" for squares occupied by the black player. The 1D array represents the game board from left to right, top to bottom (upper left square is index 0, lower right square is index 63).

The AI is expected to return it's move as the exit code, and this should be an integer index into the squares array representing the location where the AI would like to place a piece.

Again, it's expected that the AI will return a valid move - an invalid move will result in losing the game. It's also expected that the AI will return within a given amount of time (default 10s). Exceeding the timeout will result in losing the game.

Good luck!

## License

Copyright © 2015

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
