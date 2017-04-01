package com.example.betulyaman.chirp.handlers;

import android.content.Context;

import com.example.betulyaman.chirp.containers.Category;
import com.example.betulyaman.chirp.containers.SimplifiedTweet;
import com.example.betulyaman.chirp.containers.VectorElement;

import java.util.ArrayList;
import java.util.Iterator;

public class CategorizationHandler {

    private static final Integer THRESHOLD = 50;

    public static void start(Context context) {
        ArrayList<SimplifiedTweet> tweets = ConnectionHandler.getTweets();
        LanguageHandler.prepareTweet(tweets);
        DatabaseHandler databaseHandler = new DatabaseHandler(context);
        ArrayList<Category> categories = new ArrayList<>();

        // Ontolojiler uygun categorilere ekleniyor ve ontolojiye ait kelimeler de kategorinin kelimelerine ekleniyor.
        int i=0;
        for (String ontologyName : databaseHandler.getOntologyNames()) {
            categories.add(new Category(ontologyName));
            categories.get(i).setWords(databaseHandler.getEntries(ontologyName));
            i++;
        }

        distance(tweets, categories);

        tweets = null; //Ram tasarrufu


    }

    //tweetlerin ontolojilerle olan ilgisi(uzaklığı) hesaplanıyor
    public static void distance(ArrayList<SimplifiedTweet> tweets, ArrayList<Category> categories) {

        for (SimplifiedTweet tweet : tweets) {
            for (Category category : categories) {
                for (VectorElement entry : category.getWords()) {
                    if (tweet.containsWord(entry.getWord())) {
                        category.addPoint(tweet, entry.getFrequency());
                    }
                }
            }
        }
    }

    // Iterator; eleman silip ilerlerken kullanabileceğimiz tek yöntem
    // Puanları, belirlenen değerin altında kalan tweetler ilgili kategoriden siliniyor.
    public static void removeTweetsBelowThreshold(ArrayList<Category> categories) {

        for (Category c : categories) {
            Iterator<Integer> iter = c.getPoints().iterator();
            Integer i = 0;
            while (iter.hasNext()) {
                if (iter.next() < THRESHOLD) {
                    iter.remove();
                    c.getTweets().remove(i);
                } else {
                    i++;
                }
            }
        }
    }

}
