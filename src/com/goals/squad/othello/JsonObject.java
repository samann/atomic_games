package com.goals.squad.othello;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DroidOwl on 10/24/15.
 */
public class JsonObject {
    int width;
    int height;
    @SerializedName("max-index")
    int maxindex;
    String[] squares;
}