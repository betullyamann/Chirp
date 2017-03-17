package com.example.betulyaman.chirp.containers;

import com.twitter.sdk.android.core.models.Tweet;

import java.util.ArrayList;

public class SimplifiedTweet {
    private final String name;
    private final String handle;
    private final String text;
    private final ArrayList<String> parsedText;
    private final ArrayList<String> categories;

    public SimplifiedTweet(String name, String handle, String text) {
        this.name = name;
        this.handle = handle;
        this.text = text;
        parsedText = new ArrayList<>();
        categories = new ArrayList<>();
    }

    public SimplifiedTweet(Tweet tweet) {
        this.name = tweet.user.name;
        this.handle = tweet.user.screenName;
        this.text = tweet.text;
        parsedText = new ArrayList<>();
        categories = new ArrayList<>();
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

    public ArrayList<String> getParsedText() {
        return parsedText;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void addWord(String word) {
        parsedText.add(word);
    }

    public void addCatrgory(String category) {
        categories.add(category);
    }
}
