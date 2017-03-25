package com.example.betulyaman.chirp.handlers;

import android.content.Context;

import com.example.betulyaman.chirp.containers.SimplifiedTweet;

import java.util.ArrayList;

public class CategorizationHandler {

    public static void start(Context context) {
        ArrayList<SimplifiedTweet> tweets = ConnectionHandler.getTweets();
        LanguageHandler.prepareTweet(tweets);
        DatabaseHandler databaseHandler = new DatabaseHandler(context);

    }

    // TODO Weighted Euclidean distance
}
