package info.chirpapp.chirp.containers;

import com.twitter.sdk.android.core.models.Tweet;

import java.util.ArrayList;

public class SimplifiedTweet {
    private final String name;
    private final String handle;
    private final String text;
    private final ArrayList<String> words;
    private Integer belongsToACategory;

    public SimplifiedTweet(String name, String handle, String text) {
        this.name = name;
        this.handle = handle;
        this.text = text;
        words = new ArrayList<>();
        belongsToACategory = 0;
    }

    public SimplifiedTweet(Tweet tweet) {
        name = tweet.user.name;
        handle = tweet.user.screenName;
        text = tweet.text.replaceAll("(https?:|@|#)[^ ]*", " ");
        words = new ArrayList<>();
        belongsToACategory = 0;
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

    public void addWord(String word) {
        words.add(word);
    }

    public boolean containsWord(String entry) {

        return words.contains(entry);
    }

    public Integer belongsToACategory() {
        return belongsToACategory;
    }

    public void setBelonging(Integer belonging) {
        belongsToACategory += belonging;
    }

    @Override
    public String toString() {
        return name + " (" + handle + ") :  " + text;
    }
}
