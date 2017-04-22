package com.example.betulyaman.chirp.handlers;

import android.content.Context;

import com.example.betulyaman.chirp.containers.Category;
import com.example.betulyaman.chirp.containers.SimplifiedTweet;
import com.example.betulyaman.chirp.containers.VectorElement;

import java.util.ArrayList;
import java.util.Iterator;

public class CategorizationHandler {

    private static final Integer THRESHOLD = 50;

    public static ArrayList<Category> start(Context context) {
        ArrayList<SimplifiedTweet> tweets = ConnectionHandler.getTweets();
        LanguageHandler.prepareTweet(tweets);
        DatabaseHandler databaseHandler = new DatabaseHandler(context);
        ArrayList<Category> categories = new ArrayList<>();

        // Ontolojiler uygun categorilere ekleniyor ve ontolojiye ait kelimeler de kategorinin kelimelerine ekleniyor.
        int i=0;
        for (String categoryName : databaseHandler.getCategoryNames()) {
            categories.add(new Category(categoryName));
            categories.get(i).setWords(databaseHandler.getEntries(categoryName));
            i++;
        }

        distance(tweets, categories);
        tweets = null; //Ram tasarrufu

        return categories;
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

        for (Category category : categories) {
            Iterator<Integer> iterator = category.getPoints().iterator();
            Integer i = 0;
            while (iterator.hasNext()) {
                if (iterator.next() < THRESHOLD) {
                    iterator.remove();
                    category.getTweets().remove(category.getTweets().get(i));
                } else {
                    i++;
                }
            }
        }
    }

}
