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

    private static String color;
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
            color = "w"; // looking for white pieces since we are black
        } else {
            color = "b";
        }
        time = Integer.parseInt(args[2]);
    }

    public static boolean[] fill_valid_sqaures() {
        boolean[] valid = new boolean[squares.length];
        int index = -1;
        Arrays.fill(valid, false);
        for (int i = 0; i < squares.length; i++) {
            if (squares[i].equals(color)) {
                if (index % 8 > 0 || index % 8 < 7) { // check the sides
                    if ((index = check_vert(i)) > 0) {

                    }
                }
                if ((index > 0 && index < 7) || (index < 63 && index > 56)) { // top and bottom
                    if ((index = check_horz(i)) > 0) {

                    }
                } else {
                    if ((index = check_horz(i)) > 0) {
                        valid[index] = true;
                    }
                    if ((index = check_vert(i)) > 0) {
                        valid[index] = true;
                    }
                }

            }
        }

        return valid;
    }

    public static int check_horz(int index) {
     
    }

    public static int check_vert(int index) {

    }

    public static int check_diag_lr(int index) {

    }

    public static int check_diag_rl(int index) {

    }
}
