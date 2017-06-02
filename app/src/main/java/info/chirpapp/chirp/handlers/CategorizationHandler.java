package info.chirpapp.chirp.handlers;

import android.content.Context;

import java.util.ArrayList;
import java.util.Map.Entry;

import info.chirpapp.chirp.containers.Category;
import info.chirpapp.chirp.containers.SimplifiedTweet;

public class CategorizationHandler {

    private static final Integer THRESHOLD = 50;
    private final DatabaseHandler databaseHandler;

    public CategorizationHandler(Context context) {
        databaseHandler = new DatabaseHandler(context);
    }

    //tweetlerin ontolojilerle olan ilgisi(uzaklığı) hesaplanıyor
    public static void distance(ArrayList<SimplifiedTweet> tweets, ArrayList<Category> categories) {

        for (SimplifiedTweet tweet : tweets) {
            for (Category category : categories) {
                for (Entry<String, Integer> entry : category.getWords().entrySet()) {
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
/*
        for (Category category : categories) {
            Iterator<Integer> iterator = category.getTweets()..iterator();
            Integer i = 0;
            while (iterator.hasNext()) {
                if (iterator.next() < THRESHOLD) {
                    iterator.remove();
                    category.getTweets().remove(category.getTweets().get(i));
                } else {
                    i++;
                }
            }
        }*/
    }

    public ArrayList<Category> start(Context context) {
        //ArrayList<SimplifiedTweet> tweets = ConnectionHandler.getTweets();
        //LanguageHandler.prepareTweet(tweets);
        ArrayList<Category> categories = new ArrayList<>();

        // Ontolojiler uygun categorilere ekleniyor ve ontolojiye ait kelimeler de kategorinin kelimelerine ekleniyor.
        int i = 0;
        for (String categoryName : databaseHandler.getCategoryNames()) {
            categories.add(new Category(categoryName));
            categories.get(i).setWords(databaseHandler.getEntries(categoryName));
            i++;
        }

        //distance(tweets, categories);
        //tweets = null; //Ram tasarrufu

        return categories;
    }

}
