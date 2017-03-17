package com.example.betulyaman.chirp.handlers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.betulyaman.chirp.LoginActivity;
import com.example.betulyaman.chirp.containers.SimplifiedTweet;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class TwitterHandler {

    // TODO Obfuscate keys


    private Context context;
    private Activity activity;

    public TwitterHandler(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    protected void login() {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivityForResult(intent, 0);
    }

    protected void getTweets(final LinearLayout tweetView) {
        final ArrayList<SimplifiedTweet> tweets = new ArrayList<>();
        TwitterCore twitter = TwitterCore.getInstance();
        TwitterApiClient tac = twitter.getApiClient();
        StatusesService statusesService = tac.getStatusesService();
        Call<List<Tweet>> call = statusesService.homeTimeline(200, null, null, null, null, null, null);
        call.enqueue(new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> result) {
                for (Tweet tweet : result.data) {
                    tweets.add(new SimplifiedTweet(tweet));
                }
                for (SimplifiedTweet tweet : tweets) {
                    tweetView.addView(prepareTweet(tweet));
                }
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(context, exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    protected TextView prepareTweet(SimplifiedTweet tweet) {
        // TODO zemberek

        TextView msg = new TextView(context);
        msg.setEms(10);
        msg.setText(tweet.getName() + " (" + tweet.getHandle() + "): " + tweet.getText());
        msg.setPadding(10, 10, 10, 10);
        msg.setTextColor(Color.BLACK);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.START);
        params.setMargins(10, 10, 10, 10);
        msg.setLayoutParams(params);
        return msg;
    }
}
