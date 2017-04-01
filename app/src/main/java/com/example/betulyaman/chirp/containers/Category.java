package com.example.betulyaman.chirp.containers;

import com.twitter.sdk.android.core.models.Tweet;

import java.util.ArrayList;

public class Category {
    private String name;
    private ArrayList<VectorElement> words;
    private ArrayList<SimplifiedTweet> tweets;
    private ArrayList<Integer> points;

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addCategories(String ontology, ArrayList<VectorElement> entry) {
        setWords(entry);
    }

    public ArrayList<VectorElement> getWords() {
        return words;
    }

    public void setWords(ArrayList<VectorElement> words) {
        this.words = words;
    }

    public ArrayList<SimplifiedTweet> getTweets() {
        return tweets;
    }

    public void setTweets(ArrayList<SimplifiedTweet> tweets) {
        this.tweets = tweets;
    }

    public ArrayList<Integer> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Integer> points) {
        this.points = points;
    }

    public void addPoint(SimplifiedTweet tweet, Integer frequency) {
        if (!tweets.contains(tweet)) {
            tweets.add(tweet);
            points.ensureCapacity(tweets.size());
        }
        points.set(tweets.indexOf(tweet), points.get(tweets.indexOf(tweet)) + frequency);
    }
}
