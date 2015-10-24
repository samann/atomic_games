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

//        for (boolean b : test) {
//            if (b) {
//                log.log(Level.INFO, "about to exit: i = " + i);
//                System.exit(i);
//            }
//            i++;
//        }
    }

    public int find_highest_count() {
        for (int i = 0; i < counts.length; i++) {
            is_valid(i);
        }
        // FIXME: hello
        return 0;
    }
//    public static boolean[] fill_valid_sqaures() {
//        boolean[] valid = new boolean[squares.length];
//        Arrays.fill(valid, false);
//        for (int i = 0; i < squares.length; i++) {
//            log.log(Level.INFO, "i = " + i);
//            valid[i] = is_valid(i);
//        }
//        return valid;
//    }

    public static int traverse(int index, int dir) {
        int count = 0;
        while ((index > 0 && index < 63) ||
                ( (index % 8 != 7) || (index % 8 != 0) ) ) {

            index = index + dir;

            if (index < 0 || index > 63) {
                break;
            }
            if (squares[index].equals(empty)) {
                break;
            }
            if (squares[index].equals(our_color) ){
                break;
            }
            if (squares[index].equals(their_color)) {
               count++;
            }
        }
        return count;
    }

    public static int[] is_valid(int index) {

        int temp;
        if (squares[index].equals(empty)) {
            log.log(Level.INFO, "index = " + index);
            // middle of board
            if ((index % 8 > 0 && index % 8 < 7) && (index > 8 && index < 55)) {
                // below
                if (squares[index + 8].equals(their_color)) {
                    temp = traverse(index, 8);
                    if (temp > counts[index]) {
                        counts[index] = temp;
                    }
                }

                // above
                if (squares[index - 8].equals(their_color)) {
                     temp = traverse(index, -8);
                    if (temp > counts[index]) {
                        counts[index] = temp;
                    }
                }

                // right
                if (squares[index + 1].equals(their_color)) {
                    temp =  traverse(index, 1);
                    if (temp > counts[index]) {
                        counts[index] = temp;
                    }
                }

                // left
                if (squares[index - 1].equals(their_color)) {
                    temp =  traverse(index, -1);
                    if (temp > counts[index]) {
                        counts[index] = temp;
                    }
                }

                // up right
                if (squares[index - 7].equals(their_color)) {
                    temp =  traverse(index, -7);
                    if (temp > counts[index]) {
                        counts[index] = temp;
                    }
                }

                // up left
                if (squares[index - 9].equals(their_color)) {
                    temp =  traverse(index, -9);
                    if (temp > counts[index]) {
                        counts[index] = temp;
                    }
                }

                // below left
                if (squares[index + 7].equals(their_color)) {
                    temp =  traverse(index, 7);
                    if (temp > counts[index]) {
                        counts[index] = temp;
                    }
                }

                // below right
                if (squares[index + 9].equals(their_color)) {
                    temp =  traverse(index, 9);
                    if (temp > counts[index]) {
                        counts[index] = temp;
                    }
                }
                // top row
            } else if (index > 0 && index < 7) {

                // below left
                if (squares[index + 7].equals(their_color)) {
                    temp =  traverse(index, 7);
                    if (temp > counts[index]) {
                        counts[index] = temp;
                    }
                }

                // below right
                if (squares[index + 9].equals(their_color)) {
                    temp =  traverse(index, 9);
                    if (temp > counts[index]) {
                        counts[index] = temp;
                    }
                }

                // right
                if (squares[index + 1].equals(their_color)) {
                    temp =  traverse(index, 1);
                    if (temp > counts[index]) {
                        counts[index] = temp;
                    }
                }

                // left
                if (squares[index - 1].equals(their_color)) {
                    temp =  traverse(index, -1);
                    if (temp > counts[index]) {
                        counts[index] = temp;
                    }
                }

                // below
                if (squares[index + 8].equals(their_color)) {
                    temp =  traverse(index, 8);
                    if (temp > counts[index]) {
                        counts[index] = temp;
                    }
                }
                // bottom row
            } else if (index > 56 && index < 63) {
                // above
                if (squares[index - 8].equals(their_color)) {
                    temp =  traverse(index, -8);
                    if (temp > counts[index]) {
                        counts[index] = temp;
                    }
                }

                // right
                if (squares[index + 1].equals(their_color)) {
                    temp =  traverse(index, 1);
                    if (temp > counts[index]) {
                        counts[index] = temp;
                    }
                }

                // left
                if (squares[index - 1].equals(their_color)) {
                    temp =  traverse(index, -1);
                    if (temp > counts[index]) {
                        counts[index] = temp;
                    }
                }

                // up right
                if (squares[index - 7].equals(their_color)) {
                    temp =  traverse(index, -7);
                    if (temp > counts[index]) {
                        counts[index] = temp;
                    }
                }

                // up left
                if (squares[index - 9].equals(their_color)) {
                    temp =  traverse(index, -9);
                    if (temp > counts[index]) {
                        counts[index] = temp;
                    }
                }
                // left column
            } else if (index % 8 == 0) {
                if (index == 0) {
                    // below right
                    if (squares[index + 9].equals(their_color)) {
                        temp =  traverse(index, 9);
                        if (temp > counts[index]) {
                            counts[index] = temp;
                        }
                    }

                    // below
                    if (squares[index + 8].equals(their_color)) {
                        temp =  traverse(index, 8);
                        if (temp > counts[index]) {
                            counts[index] = temp;
                        }
                    }

                    // right
                    if (squares[index + 1].equals(their_color)) {
                        temp =  traverse(index, 1);
                        if (temp > counts[index]) {
                            counts[index] = temp;
                        }
                    }
                } else if (index == 56){
                    // up right
                    if (squares[index - 7].equals(their_color)) {
                        temp =  traverse(index, -7);
                        if (temp > counts[index]) {
                            counts[index] = temp;
                        }
                    }
                    // above
                    if (squares[index - 8].equals(their_color)) {
                        temp =  traverse(index, -8);
                        if (temp > counts[index]) {
                            counts[index] = temp;
                        }
                    }

                    // right
                    if (squares[index + 1].equals(their_color)) {
                        temp =  traverse(index, 1);
                        if (temp > counts[index]) {
                            counts[index] = temp;
                        }
                    }
                } else {
                    // up right
                    if (squares[index - 7].equals(their_color)) {
                        temp =  traverse(index, -7);
                        if (temp > counts[index]) {
                            counts[index] = temp;
                        }
                    }

                    // below right
                    if (squares[index + 9].equals(their_color)) {
                        temp =  traverse(index, 9);
                        if (temp > counts[index]) {
                            counts[index] = temp;
                        }
                    }

                    // below
                    if (squares[index + 8].equals(their_color)) {
                        temp =  traverse(index, 8);
                        if (temp > counts[index]) {
                            counts[index] = temp;
                        }
                    }

                    // above
                    if (squares[index - 8].equals(their_color)) {
                        temp =  traverse(index, -8);
                        if (temp > counts[index]) {
                            counts[index] = temp;
                        }
                    }

                    // right
                    if (squares[index + 1].equals(their_color)) {
                        temp =  traverse(index, 1);
                        if (temp > counts[index]) {
                            counts[index] = temp;
                        }
                    }
                }
                // right column
            } else if (index % 8 == 7) {
                if (index == 63) {
                    // above
                    if (squares[index - 8].equals(their_color)) {
                        temp =  traverse(index, -8);
                        if (temp > counts[index]) {
                            counts[index] = temp;
                        }
                    }
                    // left
                    if (squares[index - 1].equals(their_color)) {
                        temp =  traverse(index, -1);
                        if (temp > counts[index]) {
                            counts[index] = temp;
                        }
                    }
                    // up left
                    if (squares[index - 9].equals(their_color)) {
                        temp =  traverse(index, -9);
                        if (temp > counts[index]) {
                            counts[index] = temp;
                        }
                    }
                }
                else if (index == 7) {
                    // below
                    if (squares[index + 8].equals(their_color)) {
                        temp =  traverse(index, 8);
                        if (temp > counts[index]) {
                            counts[index] = temp;
                        }
                    }
                    // below left
                    if (squares[index + 7].equals(their_color)) {
                        temp =  traverse(index, 7);
                        if (temp > counts[index]) {
                            counts[index] = temp;
                        }
                    }
                    // left
                    if (squares[index - 1].equals(their_color)) {
                        temp =  traverse(index, -1);
                        if (temp > counts[index]) {
                            counts[index] = temp;
                        }
                    }
                } else {
                    // below
                    if (squares[index + 8].equals(their_color)) {
                        temp =  traverse(index, 8);
                        if (temp > counts[index]) {
                            counts[index] = temp;
                        }
                    }

                    // above
                    if (squares[index - 8].equals(their_color)) {
                        temp =  traverse(index, -8);
                        if (temp > counts[index]) {
                            counts[index] = temp;
                        }
                    }

                    // up left
                    if (squares[index - 9].equals(their_color)) {
                        temp =  traverse(index, -9);
                        if (temp > counts[index]) {
                            counts[index] = temp;
                        }
                    }

                    // below left
                    if (squares[index + 7].equals(their_color)) {
                        temp =  traverse(index, 7);
                        if (temp > counts[index]) {
                            counts[index] = temp;
                        }
                    }

                    // left
                    if (squares[index - 1].equals(their_color)) {
                        temp =  traverse(index, -1);
                        if (temp > counts[index]) {
                            counts[index] = temp;
                        }
                    }
                }
            }
        }
        return counts;
    }
}
