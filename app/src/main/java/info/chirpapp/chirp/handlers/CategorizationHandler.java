package info.chirpapp.chirp.handlers;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import info.chirpapp.chirp.containers.Category;
import info.chirpapp.chirp.containers.SimplifiedTweet;

public class CategorizationHandler {

    private static final Integer THRESHOLD = 50;
    private final DatabaseHandler databaseHandler;
    private final ConnectionHandler connectionHandler;
    private final LanguageHandler languageHandler;

    public CategorizationHandler(Context context) {
        databaseHandler = new DatabaseHandler(context);
        languageHandler = new LanguageHandler();
        connectionHandler = new ConnectionHandler();
    }

    //tweetlerin ontolojilerle olan ilgisi(uzaklığı) hesaplanıyor
    public static void distance(ArrayList<SimplifiedTweet> tweets, ArrayList<Category> categories) {

        for (SimplifiedTweet tweet : tweets) {
            for (Category category : categories) {
                for (Entry<String, Double> entry : category.getWords().entrySet()) {
                    if (tweet.containsWord(entry.getKey())) {
                        category.addPoint(tweet, entry.getValue());
                    }
                }
            }
        }
    }

    // Iterator; eleman silip ilerlerken kullanabileceğimiz tek yöntem
    // Puanları, belirlenen değerin altında kalan tweetler ilgili kategoriden siliniyor.
    public static void removeTweetsBelowThreshold(ArrayList<Category> categories) {

        for (Category category : categories) {
            Iterator<Double> iterator = category.getTweets().values().iterator();
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

    public ArrayList<Category> start(String name, HashMap<String, Double> map) {
        ArrayList<SimplifiedTweet> tweets = connectionHandler.getTweets();
        languageHandler.parseTweets(tweets);
        ArrayList<Category> categories = new ArrayList<>();


        Category category = new Category(name);
        category.setWords(map);

        categories.add(category);
        distance(tweets, categories);
        tweets = null; //Ram tasarrufu

        System.out.println(category.getTweets());

        return categories;
    }

}
