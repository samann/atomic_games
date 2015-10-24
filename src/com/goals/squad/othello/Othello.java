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
        for (String piece : board.squares) {
            System.out.println(piece);
        }

        if (args[1].equals("black")) {
        }
        if (args[1].equals("white")) {
        }
        int place_to_go = find_empty_place(board.squares);
        System.exit(place_to_go);
    }

    public static int find_empty_place(String[] board) {
        int spot = -1;

        boolean[] valid = new boolean[board.length];
        Arrays.fill(valid, false);
        for (int i = 0; i < board.length; i++) {
            if (board[i].equals("-")) {
                valid[i] = true;
                spot = i;
                break;
            }
            if (board[i].equals("b")) {
                valid[i] = false;
            }
            if (board[i].equals("w")) {
                valid[i] = false;
            }
        }
        return spot;
    }
}
