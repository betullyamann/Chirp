package info.chirpapp.chirp;

import android.app.Activity;
import android.content.Intent;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import info.chirpapp.chirp.R.id;
import info.chirpapp.chirp.R.layout;
import info.chirpapp.chirp.R.string;
import info.chirpapp.chirp.handlers.DatabaseHandler;
import info.chirpapp.chirp.handlers.OntologyHandler;
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
    private OntologyHandler ontologyHandler;
    private DatabaseHandler databaseHandler;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Uygulamaya kaldığı yerden devam etme çabası
        if (!isTaskRoot()) {
            finish();
            return;
        }

        super.onCreate(savedInstanceState);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(getApplicationContext(), new Twitter(authConfig));
        Stetho.initializeWithDefaults(this);
        ontologyHandler = new OntologyHandler(getApplicationContext());
        databaseHandler = new DatabaseHandler(getApplicationContext());
        button = new Button(getApplicationContext());

        Thread initializationThread = new Thread() {
            @Override
            public void run() {
                Log.i("TEST", "TEST");
                if (databaseHandler.getCategoryNames().size() == 0) {
//                    ontologyHandler.createOntology("spor");
//                    ontologyHandler.createOntology("siyaset");
//                    ontologyHandler.createOntology("sanat");
//                    ontologyHandler.createOntology("eğitim");
//                    ontologyHandler.createOntology("tarih");
//                    ontologyHandler.createOntology("coğrafya");
//                    ontologyHandler.createOntology("oyun");
//                    ontologyHandler.createOntology("bilim");
//                    ontologyHandler.createOntology("haberleşme");
//                    ontologyHandler.createOntology("bilgisayar");
                    ontologyHandler.createOntology("otomotiv");
                    ontologyHandler.createOntology("inşaat");
                    ontologyHandler.createOntology("yemek");
                    ontologyHandler.createOntology("tarım");
                    ontologyHandler.createOntology("mühendislik");

                }
            }
        };

        initializationThread.start();
        Activity activity = this;
        if (TwitterCore.getInstance().getSessionManager().getActiveSession() == null) {
            System.out.println("Not logged in... hmmm...");

            loginButton = new TwitterLoginButton(activity) {
                @Override
                protected Activity getActivity() {
                    System.out.println("Take this activity and make it yours.");
                    return activity;
                }
            };
            loginButton.setCallback(new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    System.out.println("SUCCESS!!!");
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

            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    loginButton.performClick();
                }
            });

            System.out.println("login finished.");
        } else {
            System.out.println("already logged in m8");
            System.out.println(TwitterCore.getInstance().getSessionManager().getActiveSession().getUserName() + " is logged in");
        }

        button.performClick();


        setContentView(layout.activity_main);

        //new Thread(() -> new OntologyHandler(getApplicationContext()).createOntology("Teknoloji")).start();
        //new Thread(() -> new OntologyHandler(getApplicationContext()).prepareNode(new Node("doğal"))).start();


        drawerLayout = (DrawerLayout) findViewById(id.layout_drawer);
        navigationView = (NavigationView) findViewById(id.view_navigation);

        try {
            initializationThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        getSupportFragmentManager().beginTransaction().replace(id.layout_frame, new TabFragment()).commit();
        navigationView.setNavigationItemSelectedListener(new OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();
                if (item.getItemId() == id.twitter_log_out) {
                    Twitter.getSessionManager().clearActiveSession();
                    Twitter.logOut();
                    System.out.println("Not logged in... hmmm...");
                    loginButton = new TwitterLoginButton(activity) {
                        @Override
                        protected Activity getActivity() {
                            System.out.println("Take this activity and make it yours.");
                            return activity;
                        }
                    };
                    loginButton.setCallback(new Callback<TwitterSession>() {
                        @Override
                        public void success(Result<TwitterSession> result) {
                            System.out.println("SUCCESS!!!");
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

                    button.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            loginButton.performClick();
                        }
                    });

                    System.out.println("login finished.");
                }
                if (item.getItemId() == id.ontology_settings) {
                }
                return false;
            }
        });
        navigationView.getHeaderView(0).findViewById(id.header_image).setOnClickListener(v -> {
            if (TwitterCore.getInstance().getSessionManager().getActiveSession() == null) {
                System.out.println("Not logged in... hmmm...");
                loginButton = new TwitterLoginButton(activity) {
                    @Override
                    protected Activity getActivity() {
                        System.out.println("Take this activity and make it yours.");
                        return activity;
                    }
                };
                loginButton.setCallback(new Callback<TwitterSession>() {
                    @Override
                    public void success(Result<TwitterSession> result) {
                        System.out.println("SUCCESS!!!");
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

                button.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loginButton.performClick();
                    }
                });

                System.out.println("login finished.");
            } else {
                System.out.println("already logged in m8");
                System.out.println(TwitterCore.getInstance().getSessionManager().getActiveSession().getUserName() + " is logged in");
            }
        });
        if (TwitterCore.getInstance().getSessionManager().getActiveSession() != null) {
            ((TextView) navigationView.getHeaderView(0).findViewById(id.drawer_username)).setText(TwitterCore.getInstance().getSessionManager().getActiveSession().getUserName());
            ((TextView) navigationView.getHeaderView(0).findViewById(id.drawer_handle)).setText(String.valueOf(TwitterCore.getInstance().getSessionManager().getActiveSession().getUserId()));


            toolbar = (Toolbar) findViewById(id.toolbar);
            ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, string.app_name, string.app_name);
            drawerLayout.addDrawerListener(drawerToggle);
            drawerToggle.syncState();


        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Make sure that the loginButton hears the result from any
        // Activity that it triggered.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }
}