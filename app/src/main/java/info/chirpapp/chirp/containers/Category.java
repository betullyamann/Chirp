package info.chirpapp.chirp.containers;

import java.util.HashMap;

public class Category {
    private String name;
    private HashMap<String, Double> words;
    private HashMap<SimplifiedTweet, Double> tweets;

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Double> getWords() {
        return words;
    }

    public void setWords(HashMap<String, Double> words) {
        this.words = words;
    }

    public HashMap<SimplifiedTweet, Double> getTweets() {
        return tweets;
    }

    public void setTweets(HashMap<SimplifiedTweet, Double> tweets) {
        this.tweets = tweets;
    }

    public void addPoint(SimplifiedTweet tweet, Double frequency) {
        if (tweets.containsKey(tweet)) {
            tweets.put(tweet, tweets.get(tweet) + frequency);
        } else {
            tweets.put(tweet, frequency);
            tweet.setBelongingTrue();
        }

    }
}
