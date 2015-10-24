package com.goals.squad.othello;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Othello {

    private static final Logger log = Logger.getLogger(Othello.class.getName());

    private static String their_color;
    private static String our_color;
    private static final String empty = "-";
    private static int time;
    private static String[] squares;

    private static int[] counts;

    public static void main(String[] args) {
        // write your code here
        FileHandler fh;

        try {
            fh = new FileHandler("log");
            log.addHandler(fh);
            SimpleFormatter sf = new SimpleFormatter();
            fh.setFormatter(sf);
            log.info("success");
        } catch (IOException e) {
            System.out.print("error setting up log");
        }

        if (args.length < 3) {
            log.log(Level.SEVERE, "not enough args");
            return;
        }

        Gson gson = new Gson();
        JsonObject board = gson.fromJson(args[0], JsonObject.class);

        System.out.println("Height: " + board.height);
        System.out.println("Width: " + board.width);
        System.out.println("Max: " + board.maxindex);
        squares = board.squares;

        if (args[1].equals("black")) {
            their_color = "w"; // looking for white pieces since we are black
            our_color = "b";
        } else {
            their_color = "b";
            our_color = "w";
        }
        time = Integer.parseInt(args[2]);
        counts = new int[squares.length];
        Arrays.fill(counts, 0);
        for (int i = 0; i < squares.length; i++) {
            is_valid(i);
        }
        System.exit(find_highest_count());
    }

    public static int find_highest_count() {
        int temp = 0, index = -1;
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] > temp) {
                temp = counts[i];
                index = i;
            }
        }
        return index;
    }

    public static int traverse(int index, int dir) {
        boolean traversed = false;
        int count = 0;
        log.log(Level.INFO, "index = " + index + " count = " + counts[index]);
        while ((index > 0 && index < 63) ||
                ((index % 8 != 7) || (index % 8 != 0)) && !traversed) {

            index = index + dir;

            if (index < 0 || index > 63) {
                break;
            }
            if (squares[index].equals(empty)) {
                traversed = true;
            } else if (squares[index].equals(our_color)) {
                traversed = true;
            } else if (squares[index].equals(their_color)) {
                traversed = false;
                count++;
            }
        }
        return count;
    }

    public static boolean is_valid(int index) {
        // return direction
        if (squares[index].equals(empty)) {

            // middle of board
            if ((index % 8 > 0 && index % 8 < 7) && (index > 8 && index < 55)) {
                // below
                if (squares[index + 8].equals(their_color)) {
                    counts[index] += traverse(index, 8);
                }

                // above
                if (squares[index - 8].equals(their_color)) {
                    counts[index] += traverse(index, -8);
                }

                // right
                if (squares[index + 1].equals(their_color)) {
                    counts[index] += traverse(index, 1);
                }

                // left
                if (squares[index - 1].equals(their_color)) {
                    counts[index] += traverse(index, -1);
                }

                // up right
                if (squares[index - 7].equals(their_color)) {
                    counts[index] += traverse(index, -7);
                }

                // up left
                if (squares[index - 9].equals(their_color)) {
                    counts[index] += traverse(index, -9);
                }

                // below left
                if (squares[index + 7].equals(their_color)) {
                    counts[index] += traverse(index, 7);
                }

                // below right
                if (squares[index + 9].equals(their_color)) {
                    counts[index] += traverse(index, 9);
                }
                // top row
            } else if (index > 0 && index < 7) {

                // below left
                if (squares[index + 7].equals(their_color)) {
                    counts[index] += traverse(index, 7);
                }

                // below right
                if (squares[index + 9].equals(their_color)) {
                    counts[index] += traverse(index, 9);
                }

                // right
                if (squares[index + 1].equals(their_color)) {
                    counts[index] += traverse(index, 1);
                }

                // left
                if (squares[index - 1].equals(their_color)) {
                    counts[index] += traverse(index, -1);
                }

                // below
                if (squares[index + 8].equals(their_color)) {
                    counts[index] += traverse(index, 8);
                }
                // bottom row
            } else if (index > 56 && index < 63) {
                // above
                if (squares[index - 8].equals(their_color)) {
                    counts[index] += traverse(index, -8);
                }

                // right
                if (squares[index + 1].equals(their_color)) {
                    counts[index] += traverse(index, 1);
                }

                // left
                if (squares[index - 1].equals(their_color)) {
                    counts[index] += traverse(index, -1);
                }

                // up right
                if (squares[index - 7].equals(their_color)) {
                    counts[index] += traverse(index, -7);
                }

                // up left
                if (squares[index - 9].equals(their_color)) {
                    counts[index] += traverse(index, -9);
                }
                // left column
            } else if (index % 8 == 0) {
                if (index == 0) {
                    // below right
                    if (squares[index + 9].equals(their_color)) {
                        counts[index] += traverse(index, 9);
                    }

                    // below
                    if (squares[index + 8].equals(their_color)) {
                        counts[index] += traverse(index, 8);
                    }

                    // right
                    if (squares[index + 1].equals(their_color)) {
                        counts[index] += traverse(index, 1);
                    }
                } else if (index == 56) {
                    // up right
                    if (squares[index - 7].equals(their_color)) {
                        counts[index] += traverse(index, -7);
                    }
                    // above
                    if (squares[index - 8].equals(their_color)) {
                        counts[index] += traverse(index, -8);
                    }

                    // right
                    if (squares[index + 1].equals(their_color)) {
                        counts[index] += traverse(index, 1);
                    }
                } else {
                    // up right
                    if (squares[index - 7].equals(their_color)) {
                        counts[index] += traverse(index, -7);
                    }

                    // below right
                    if (squares[index + 9].equals(their_color)) {
                        counts[index] += traverse(index, 9);
                    }

                    // below
                    if (squares[index + 8].equals(their_color)) {
                        counts[index] += traverse(index, 8);
                    }

                    // above
                    if (squares[index - 8].equals(their_color)) {
                        counts[index] += traverse(index, -8);
                    }

                    // right
                    if (squares[index + 1].equals(their_color)) {
                        counts[index] += traverse(index, 1);
                    }
                }
            } else if (index % 8 == 7) {
                if (index == 63) {
                    // above
                    if (squares[index - 8].equals(their_color)) {
                        counts[index] += traverse(index, -8);
                    }
                    // left
                    if (squares[index - 1].equals(their_color)) {
                        counts[index] += traverse(index, -1);
                    }
                    // up left
                    if (squares[index - 9].equals(their_color)) {
                        counts[index] += traverse(index, -9);
                    }
                } else if (index == 7) {
                    // below
                    if (squares[index + 8].equals(their_color)) {
                        counts[index] += traverse(index, 8);
                    }
                    // below left
                    if (squares[index + 7].equals(their_color)) {
                        counts[index] += traverse(index, 7);
                    }
                    // left
                    if (squares[index - 1].equals(their_color)) {
                        counts[index] += traverse(index, -1);
                    }
                } else {
                    // below
                    if (squares[index + 8].equals(their_color)) {
                        counts[index] += traverse(index, 8);
                    }

                    // above
                    if (squares[index - 8].equals(their_color)) {
                        counts[index] += traverse(index, -8);
                    }

                    // up left
                    if (squares[index - 9].equals(their_color)) {
                        counts[index] += traverse(index, -9);
                    }

                    // below left
                    if (squares[index + 7].equals(their_color)) {
                        counts[index] += traverse(index, 7);
                    }

                    // left
                    if (squares[index - 1].equals(their_color)) {
                        counts[index] += traverse(index, -1);
                    }
                }
            }
        }
        return (counts[index] > 0);
    }
}
