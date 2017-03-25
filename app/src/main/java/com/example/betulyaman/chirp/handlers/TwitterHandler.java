package com.example.betulyaman.chirp.handlers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.example.betulyaman.chirp.LoginActivity;

public class TwitterHandler {

    // TODO Obfuscate keys


    private Context context;
    private Activity activity;

    public TwitterHandler(Activity activity) {
        this.activity = activity;
    }


    public void login() {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivityForResult(intent, 0);
    }
}
