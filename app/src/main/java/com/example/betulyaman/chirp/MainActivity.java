package com.example.betulyaman.chirp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.betulyaman.chirp.handlers.OntologyHandler;
import com.example.betulyaman.chirp.handlers.TwitterHandler;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.

    private static final String TWITTER_KEY = "832KLZNI1BvYOh3ysVRBtLgHV";
    private static final String TWITTER_SECRET = "6Karh2h3wjXpvbAB2XpUvbwSo9zdgchETeZW6IB20Ilq09zQhJ";

    Button button;
    Button login;
    EditText editText;
    EditText output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final TwitterHandler twitterHandler = new TwitterHandler(this);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(getApplicationContext(), new Twitter(authConfig));
        setContentView(R.layout.activity_main);

        output = (EditText) findViewById(R.id.output);
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                twitterHandler.login();
//                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                startActivityForResult(intent, 0);
            }
        });

        button = (Button) findViewById(R.id.button);
        editText = (EditText) findViewById(R.id.editText);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyTask().execute(editText.getText().toString());
            }
        });

        /* Dallanılan threadi bulmak için context*/

        button.performClick();


    }

    private class MyTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... voids) {
            OntologyHandler.;

            return null;
        }
    }


}
