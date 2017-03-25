package com.example.betulyaman.chirp.handlers;

import android.content.Context;

import com.example.betulyaman.chirp.containers.SimplifiedTweet;
import com.example.betulyaman.chirp.containers.VectorElement;

import java.util.ArrayList;

public class CategorizationHandler {

    public static void start(Context context) {
        ArrayList<SimplifiedTweet> tweets = ConnectionHandler.getTweets();
        LanguageHandler.prepareTweet(tweets);
        DatabaseHandler databaseHandler = new DatabaseHandler(context);
    }

    //tweetlerin ontolojilerle olan ilgisi(uzaklığı) hesaplanıyor
    public static void distance(ArrayList<SimplifiedTweet> tweets, DatabaseHandler databaseHandler) {
        for (SimplifiedTweet tweet : tweets) {
            for (String ontology : databaseHandler.getOntologies()) {
                tweet.addCategory(ontology);
                for (VectorElement entry : databaseHandler.getEntries(ontology)) {
                    if (tweet.containsWord(entry.getWord())) {
                        tweet.addPoint(ontology, entry.getFrequency());
                    }
                }
            }

        }
    }



    // TODO Weighted Euclidean distance
}
