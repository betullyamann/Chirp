package info.chirpapp.chirp.handlers;

import android.content.Context;

import com.twitter.sdk.android.core.TwitterCore;

import java.util.ArrayList;
import java.util.Map.Entry;

import info.chirpapp.chirp.containers.Category;
import info.chirpapp.chirp.containers.SimplifiedTweet;

public class CategorizationHandler {


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
            System.out.println("____________________");
            System.out.println("TWEET: " + tweet);
            for (Category category : categories) {
                for (Entry<String, Integer> entry : category.getWords().entrySet()) {
                    System.out.println();
                    if (tweet.containsWord(entry.getKey())) {
                        category.addPoint(tweet, entry.getValue());
                        //System.out.println("ENTRY KEY " + entry.getKey() +" POİNT :" + category.getPoint(tweet));
                    }
                }
                if (category.getTweets().containsKey(tweet)) {
                    point += category.getPoint(tweet);
                }
            }

            //System.out.println("point " + point + " size " + categories.size());
            point /= categories.size();
            //System.out.println("avg point " + point);
            for (Category category : categories) {
                if (category.getTweets().containsKey(tweet)) {
                    if (category.getPoint(tweet) < point) {
                        category.getTweets().remove(tweet);
                    } else {
                        //System.out.println("CATEGORY " + category + " POINT " + category.getTweets().get(tweet));
                    }
                }
            }
            point = 0;

        }
    }

    public ArrayList<Category> start() {
        while(TwitterCore.getInstance().getSessionManager().getActiveSession() == null){

        }

        ArrayList<SimplifiedTweet> tweets = connectionHandler.getTweets();
        languageHandler.parseTweets(tweets);
        ArrayList<Category> categories = new ArrayList<>();

        ArrayList<String> categoryNames = databaseHandler.getCategoryNames();
        System.out.println("categoryNames: " + categoryNames);


        for (String categoryName : categoryNames) {
            categories.add(new Category(categoryName, databaseHandler.getEntries(categoryName)));
        }

        for (Category category : categories) {
            System.out.println(category);
        }
        distance(tweets, categories);

        return categories;
    }

}
