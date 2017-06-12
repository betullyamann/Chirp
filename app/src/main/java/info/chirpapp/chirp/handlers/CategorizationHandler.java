package info.chirpapp.chirp.handlers;

import android.content.Context;

import com.twitter.sdk.android.core.TwitterCore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
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

        for (SimplifiedTweet tweet : tweets) {
            int point = 0;
            int counter;   // Tweetlerin alakalı olduğu kategori sayısını tutmak için kullanılıyor.
            int wordCount = 0; // Tweetin, alakalı olduğu kategoriye ait kaç kelime içerdiğini tutmak için kullanılıyor.

            counter = 0;
            int i = 0;

            System.out.println("____________________");
            System.out.print(" TWEET: " + tweet + " \nwords " + tweet.getWords()+ " %%%%%% ");
            for (Category category : categories) {
                wordCount = 0;
                Iterator<Entry<String, Integer>> iterator = category.getWords().entrySet().iterator();
                System.out.print("- CATEGORY " + category.getName() + " - ılk 5 - " + " %%%%%% ");
                while (iterator.hasNext() && i < 5) {
                    Entry<String, Integer> entry = iterator.next();
                    if (tweet.containsWord(entry.getKey())) {
                        wordCount += 3;

                        category.addPoint(tweet, entry.getValue());
                        tweet.setBelonging(1);

                        System.out.print(" TWEET CONTAINS WORD " + entry.getKey() + " %%%%%% " + " WORDCOUNT " + wordCount + " %%%%%% " + " TWEET CATEGORY POINT " + category.getPoint(tweet) + " %%%%%% ");
                        // System.out.println("ENTRY KEY " + entry.getKey() +" POINT :" + category.getPoint(tweet));
                    }
                    i++;
                }

                System.out.print(" - sonrakiler - " + " %%%%%% ");
                while (iterator.hasNext()) {
                    Entry<String, Integer> entry = iterator.next();
                    if (tweet.containsWord(entry.getKey())) {
                        wordCount++;
                        category.addPoint(tweet, entry.getValue());
                        tweet.setBelonging(1);
                        System.out.print(" TWEET CONTAINS WORD " + entry.getKey() + " %%%%%% " + " WORDCOUNT " + wordCount + " %%%%%% " + " TWEET CATEGORY POINT " + category.getPoint(tweet) + " %%%%%% ");
                        // System.out.println("ENTRY KEY " + entry.getKey() +" POINT :" + category.getPoint(tweet));
                    }
                }


                if (category.getTweets().containsKey(tweet)) {
                    counter++; // tweetin eşletiği kategori sayısı
                    System.out.print(" tweetin eşletiği kategori sayısı COUNTER " + counter + " %%%%%% ");
                    point += category.getPoint(tweet); //tweetin kategorilerden kazandığı toplam puan
                    System.out.print(" tweetin kategorilerden kazandığı toplam puan POINT" + point + " %%%%%% " );
                }
            }

            if (counter != 0) {
                point /= counter; // tweet puanı içerildiği kategori sayısına bölünüyor
                for (Category category : categories) {
                    if (category.getTweets().containsKey(tweet)) {
                        if(category.getPoint(tweet) > point){
                            System.out.print("CATEGORY " + category.getName() + " TWEET " + tweet + " %%%%%% " + " POINT " + category.getPoint(tweet) +" > " + point + " %%%%%% ");
                            if(wordCount >= 2 ) {
                                System.out.print(" COUNT " + wordCount +" >= " + 2 + " %%%%%% ");
                            }
                        }

                        if (category.getPoint(tweet) < point && wordCount < 2) {
                            System.out.print(" CATEGORY " + category.getName() + " REMOVE " + tweet + " %%%%%% " );
                            category.getTweets().remove(tweet);
                            tweet.setBelonging(-1);
                        }

                    }
                }
            }

            System.out.print(" NEXT TWEET " );
        }
    }

    public ArrayList<Category> start() {
        while (TwitterCore.getInstance().getSessionManager().getActiveSession() == null) {

        }

        ArrayList<SimplifiedTweet> tweets = connectionHandler.getTweets();
        languageHandler.parseTweets(tweets);
        ArrayList<Category> categories = new ArrayList<>();
        ArrayList<String> categoryNames = databaseHandler.getCategoryNames();
        Category unCategorized = new Category("unCategorized", null);


        for (String categoryName : databaseHandler.getCategoryNames()) {
            categories.add(new Category(categoryName, databaseHandler.getEntries(categoryName)));
        }

        for (Category category : categories) {
            System.out.println(category);
        }
        distance(tweets, categories);

        categories.add(unCategorized);
        for(SimplifiedTweet tweet : tweets){
            if(tweet.belongsToACategory() <= 0){
                unCategorized.getTweets().put(tweet, 1);
            }
        }

        return categories;
    }
}
