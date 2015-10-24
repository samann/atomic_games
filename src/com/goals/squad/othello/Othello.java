package com.goals.squad.othello;

import com.google.gson.Gson;

import java.io.IOException;
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

    }
}
