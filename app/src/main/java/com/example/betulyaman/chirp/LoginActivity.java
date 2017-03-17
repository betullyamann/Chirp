package com.example.betulyaman.chirp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class LoginActivity extends AppCompatActivity {

    private TwitterLoginButton loginButton;
    private TextView textView;
    private static final int LOGIN_OK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textView = (TextView) findViewById(R.id.textView);
        if (TwitterCore.getInstance().getSessionManager().getActiveSession() != null) {
            String tmp = "User " + TwitterCore.getInstance().getSessionManager().getActiveSession().getUserName() + " is logged in.";
            textView.append(tmp);
        } else {
            String tmp = "Nobody is logged in.";
            textView.append(tmp);
        }


        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession session = result.data;
                setResult(LOGIN_OK);
                finish();
            }

            @Override
            public void failure(TwitterException exception) {
                setResult(exception.getMessage().hashCode());
                Log.d("TwitterKit", "Login with Twitter failure", exception);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Make sure that the loginButton hears the result from any
        // Activity that it triggered.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }


}
