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

    private Activity activity;

    public TwitterHandler(Activity activity) {
        this.activity = activity;
    }

    public void login() {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivityForResult(intent, 0);
    }

    public static void prepareTweet(SimplifiedTweet tweet) {

    }
}
