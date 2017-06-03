package info.chirpapp.chirp.containers;

import java.util.HashMap;

public class Category {
    private String name;
    private HashMap<String, Integer> words;
    private HashMap<SimplifiedTweet, Integer> tweets;

    public Category(String name, HashMap<String, Integer> words) {
        this.name = name;
        this.words = words;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Integer> getWords() {
        return words;
    }

    public void setWords(HashMap<String, Integer> words) {
        this.words = words;
    }

    public HashMap<SimplifiedTweet, Integer> getTweets() {
        return tweets;
    }

    public void setTweets(HashMap<SimplifiedTweet, Integer> tweets) {
        this.tweets = tweets;
    }

    public void addPoint(SimplifiedTweet tweet, Integer frequency) {
        if (tweets.containsKey(tweet)) {
            tweets.put(tweet, tweets.get(tweet) + frequency);
        } else {
            tweets.put(tweet, frequency);
            tweet.setBelongingTrue();
        }
    }

    public Integer getPoint(SimplifiedTweet tweet){
        return tweets.get(tweet);
    }
}
