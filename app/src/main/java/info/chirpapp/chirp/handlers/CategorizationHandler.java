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
        int point = 0;
        for (SimplifiedTweet tweet : tweets) {
            for (Category category : categories) {
                for (Entry<String, Integer> entry : category.getWords().entrySet()) {
                    if (tweet.containsWord(entry.getKey())) {
                        category.addPoint(tweet, entry.getValue());
                    }
                }
                point += category.getPoint(tweet);
            }

            point /= categories.size();
            for(Category category : categories){
                if(category.getPoint(tweet) < point ) {
                    category.getTweets().remove(tweet);
                }
            }

            for(Category category : categories){
                System.out.println("CATEGORY : " + category + '\n' + "TWEET: " + category.getTweets());
            }

            point = 0;
        }
    }

    public ArrayList<Category> start() {
        ArrayList<SimplifiedTweet> tweets = connectionHandler.getTweets();
        languageHandler.parseTweets(tweets);
        ArrayList<Category> categories =  new ArrayList<>();

        ArrayList<String> categoryNames = databaseHandler.getCategoryNames();
        for(String categoryName : categoryNames){
            categories.add(new Category(categoryName, databaseHandler.getEntries(categoryName)));
        }

        distance(tweets, categories);
        tweets = null; //Ram tasarrufu

        return categories;
    }

}
