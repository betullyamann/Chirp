package com.example.betulyaman.chirp.handlers;

import com.example.betulyaman.chirp.containers.SimplifiedTweet;

import java.util.ArrayList;

public class CategorizationHandler {

    public static void start() {
        ArrayList<SimplifiedTweet> tweets = ConnectionHandler.getTweets();
        LanguageHandler.prepareTweet(tweets);

    }

    // TODO Weighted Euclidean distance
}
