package com.example.betulyaman.chirp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.betulyaman.chirp.R.id;
import com.example.betulyaman.chirp.R.layout;
import com.example.betulyaman.chirp.R.string;
import com.example.betulyaman.chirp.handlers.OntologyHandler;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "832KLZNI1BvYOh3ysVRBtLgHV";
    private static final String TWITTER_SECRET = "6Karh2h3wjXpvbAB2XpUvbwSo9zdgchETeZW6IB20Ilq09zQhJ";
    TwitterLoginButton loginButton;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Uygulamaya kaldığı yerden devam etme çabası
        if (!isTaskRoot()) {
            finish();
            return;
        }


        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(getApplicationContext(), new Twitter(authConfig));

        twitterLogin();
        Log.i("TEST", "TEST");

        drawerLayout = (DrawerLayout) findViewById(id.layout_drawer);
        navigationView = (NavigationView) findViewById(id.view_navigation);

        getSupportFragmentManager().beginTransaction().replace(id.layout_frame, new TabFragment()).commit();

        navigationView.setNavigationItemSelectedListener(new OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();

                if (item.getItemId() == id.twitter_log_out) {

                }
                if (item.getItemId() == id.ontology_settings) {

                }
                if (item.getItemId() == id.statistics) {

                }
                return false;
            }
        });

        toolbar = (Toolbar) findViewById(id.toolbar);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, string.app_name, string.app_name);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    private void twitterLogin() {
        if (TwitterCore.getInstance().getSessionManager().getActiveSession() != null) {
            Toast.makeText(getApplicationContext(), "User " + TwitterCore.getInstance().getSessionManager().getActiveSession().getUserName() + " is logged in.", Toast.LENGTH_LONG).show();

        } else {
            final Activity activity = this;
            loginButton = new TwitterLoginButton(activity) {
                @Override
                protected Activity getActivity() {
                    return activity;
                }
            };

            loginButton.setCallback(new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    // The TwitterSession is also available through:
                    // Twitter.getInstance().core.getSessionManager().getActiveSession()
                    TwitterSession session = result.data;
                    // TODO: Remove toast and use the TwitterSession's userID
                    // with your app's user model
                    String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                }

                @Override
                public void failure(TwitterException exception) {
                    Log.d("TwitterKit", "Login with Twitter failure", exception);
                }
            });
            Log.i("LOGIN", "OLDU");
            loginButton.performClick();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Make sure that the loginButton hears the result from any
        // Activity that it triggered.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

    private class MyTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... voids) {
            OntologyHandler.start();

            return null;
        }
    }
}
