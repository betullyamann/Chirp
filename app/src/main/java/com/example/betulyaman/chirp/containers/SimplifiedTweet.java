package com.example.betulyaman.chirp.containers;

import com.twitter.sdk.android.core.models.Tweet;

import java.util.ArrayList;
import java.util.Collection;

public class SimplifiedTweet {
    private final String name;
    private final String handle;
    private final String text;
    private final ArrayList<String> words;
    private final ArrayList<String> categories;
    private final ArrayList<Integer> points;

    public SimplifiedTweet(String name, String handle, String text) {
        this.name = name;
        this.handle = handle;
        this.text = text;
        words = new ArrayList<>();
        categories = new ArrayList<>();
        points = new ArrayList<>();
    }

    public SimplifiedTweet(Tweet tweet) {
        this.name = tweet.user.name;
        this.handle = tweet.user.screenName;
        this.text = tweet.text;
        words = new ArrayList<>();
        categories = new ArrayList<>();
        points = new ArrayList<>();
    }


    public String getName() {
        return name;
    }

    public String getHandle() {
        return handle;
    }

    public String getText() {
        return text;
    }

    public ArrayList<String> getWords() {
        return words;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void addWord(String word) {
        words.add(word);
    }

    public void addCategory(String category) {
        categories.add(category);
    }

    public boolean containsWord(String entry) {

        return words.contains(entry);
    }

    public void addPoint(String ontology, Integer frequency){
        points.set(categories.indexOf(ontology), points.get(categories.indexOf(ontology))+frequency);
    }


}
