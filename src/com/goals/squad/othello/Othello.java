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
    }

//    public static boolean[] fill_valid_sqaures() {
//        boolean[] valid = new boolean[squares.length];
//        int index = -1;
//        Arrays.fill(valid, false);
//        for (int i = 1; i < squares.length - 1; i++) {
//            // find a vlid spot based on one square
//            if (squares[i].equals(their_color)) {
//                if (index % 8 > 0 || index % 8 < 7) { // check the sides
//                    if ((index = check_vert(i)) > 0) {
//
//                    }
//                }
//                if ((index > 0 && index < 7) || (index < 63 && index > 56)) { // top and bottom
//                    if ((index = check_horz(i)) > 0) {
//
//                    }
//                } else {
//                    if ((index = check_horz(i)) > 0) {
//                        valid[index] = true;
//                    }
//                    if ((index = check_vert(i)) > 0) {
//                        valid[index] = true;
//                    }
//                }
//
//            }
//        }
//
//        return valid;
//    }
    /**
     * if our color is on one side and empty onthe other
     */
    public static int check_horz(int index) {
        if (squares[index + 1].equals(our_color) && squares[index - 1].equals(empty)) {
            return index - 1;
        }
        if (squares[index - 1].equals(our_color) && squares[index + 1].equals(empty)) {
            return index + 1;
        }
        return -1;
    }

    public static int check_vert(int index) {
        if (squares[index + 8].equals(our_color) && squares[index - 8].equals(empty)) {
            return index - 8;
        }
        if (squares[index - 8].equals(our_color) && squares[index + 8].equals(empty)) {
            return index + 8;
        }
        return -1;
    }

    public static int check_diag_lr(int index) {

        return -1;
    }

    public static int check_diag_rl(int index) {

        return -1;
    }
    public static boolean[] fill_valid_sqaures() {
        boolean[] valid = new boolean[squares.length];
        int index = -1;
        Arrays.fill(valid, false);
        for (int i = 0; i < squares.length - 1; i++) {
            valid[i] = is_valid(i);
        }
        return valid;
    }

    public static boolean traverse(int index, int dir) {
        while ((index > 0 && index < 63) ||
                ( (index % 8 != 7) || (index % 8 != 0) ) &&
                        squares[index + dir].equals(their_color)) {
            if (squares[index + dir].equals(our_color) ){
                return true;
            }
            if (squares[index + dir].equals(empty)) {
                return false;
            }
            index = index + dir;
        }
        return false;
    }

    public static boolean is_valid(int index) {
        if (squares[index].equals(empty)) {
            // middle of board
            if (index % 8 > 0 || index % 8 < 7) {

                // below
                if (squares[index + 8].equals(their_color)) {
                    return traverse(index, 8);
                }

                // above
                if (squares[index - 8].equals(their_color)) {
                    return traverse(index, -8);
                }

                // right
                if (squares[index + 1].equals(their_color)) {
                    return traverse(index, 1);
                }

                // left
                if (squares[index - 1].equals(their_color)) {
                    return traverse(index, -1);
                }

                // up right
                if (squares[index - 7].equals(their_color)) {
                    return traverse(index, -7);
                }

                // up left
                if (squares[index - 9].equals(their_color)) {
                    return traverse(index, -9);
                }

                // below left
                if (squares[index + 7].equals(their_color)) {
                    return traverse(index, 7);
                }

                // below right
                if (squares[index + 9].equals(their_color)) {
                    return traverse(index, 9);
                }
                // top row
            } else if (index > 0 && index < 7) {

                // below left
                if (squares[index + 7].equals(their_color)) {
                    return traverse(index, 7);
                }

                // below right
                if (squares[index + 9].equals(their_color)) {
                    return traverse(index, 9);
                }

                // right
                if (squares[index + 1].equals(their_color)) {
                    return traverse(index, 1);
                }

                // left
                if (squares[index - 1].equals(their_color)) {
                    return traverse(index, -1);
                }

                // below
                if (squares[index + 8].equals(their_color)) {
                    return traverse(index, 8);
                }
                // bottom row
            } else if (index > 56 && index < 63) {
                // above
                if (squares[index - 8].equals(their_color)) {
                    return traverse(index, -8);
                }

                // right
                if (squares[index + 1].equals(their_color)) {
                    return traverse(index, 1);
                }

                // left
                if (squares[index - 1].equals(their_color)) {
                    return traverse(index, -1);
                }

                // up right
                if (squares[index - 7].equals(their_color)) {
                    return traverse(index, -7);
                }

                // up left
                if (squares[index - 9].equals(their_color)) {
                    return traverse(index, -9);
                }
                // left column
            } else if (index % 8 == 0) {
                if (index == 0) {
                    // below right
                    if (squares[index + 9].equals(their_color)) {
                        return traverse(index, 9);
                    }

                    // below
                    if (squares[index + 8].equals(their_color)) {
                        return traverse(index, 8);
                    }

                    // right
                    if (squares[index + 1].equals(their_color)) {
                        return traverse(index, 1);
                    }
                } else if (index == 56){
                    // up right
                    if (squares[index - 7].equals(their_color)) {
                        return traverse(index, -7);
                    }
                    // above
                    if (squares[index - 8].equals(their_color)) {
                        return traverse(index, -8);
                    }

                    // right
                    if (squares[index + 1].equals(their_color)) {
                        return traverse(index, 1);
                    }
                } else {
                    // up right
                    if (squares[index - 7].equals(their_color)) {
                        return traverse(index, -7);
                    }

                    // below right
                    if (squares[index + 9].equals(their_color)) {
                        return traverse(index, 9);
                    }

                    // below
                    if (squares[index + 8].equals(their_color)) {
                        return traverse(index, 8);
                    }

                    // above
                    if (squares[index - 8].equals(their_color)) {
                        return traverse(index, -8);
                    }

                    // right
                    if (squares[index + 1].equals(their_color)) {
                        return traverse(index, 1);
                    }
                }
            } else if (index % 8 == 7) {
                if (index == 63) {
                    // above
                    if (squares[index - 8].equals(their_color)) {
                        return traverse(index, -8);
                    }
                    // left
                    if (squares[index - 1].equals(their_color)) {
                        return traverse(index, -1);
                    }
                    // up left
                    if (squares[index - 9].equals(their_color)) {
                        return traverse(index, -9);
                    }
                }
                else if (index == 7) {
                    // below
                    if (squares[index + 8].equals(their_color)) {
                        return traverse(index, 8);
                    }
                    // below left
                    if (squares[index + 7].equals(their_color)) {
                        return traverse(index, 7);
                    }
                    // left
                    if (squares[index - 1].equals(their_color)) {
                        return traverse(index, -1);
                    }
                } else {
                    // below
                    if (squares[index + 8].equals(their_color)) {
                        return traverse(index, 8);
                    }

                    // above
                    if (squares[index - 8].equals(their_color)) {
                        return traverse(index, -8);
                    }

                    // up left
                    if (squares[index - 9].equals(their_color)) {
                        return traverse(index, -9);
                    }

                    // below left
                    if (squares[index + 7].equals(their_color)) {
                        return traverse(index, 7);
                    }

                    // left
                    if (squares[index - 1].equals(their_color)) {
                        return traverse(index, -1);
                    }
                }
            }
        }
        return false;
    }
}
