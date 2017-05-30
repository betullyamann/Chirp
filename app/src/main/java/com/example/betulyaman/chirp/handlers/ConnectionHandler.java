package com.example.betulyaman.chirp.handlers;

import android.util.Log;

import com.example.betulyaman.chirp.containers.Node;
import com.example.betulyaman.chirp.containers.SimplifiedTweet;
import com.example.betulyaman.chirp.containers.WikiAvailability;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;


public class ConnectionHandler {

    private static final Integer MAX_TWEET_COUNT = 200;
    private static final String TDK_TERM_PATTERN = "[1-9]\\..*<br>";

    private final ConnectionService wikiService;
    private final ConnectionService TDKService;
    private final StatusesService statusesService;

    public ConnectionHandler() {
        wikiService = new Builder().baseUrl("http://tr.wikipedia.org/").addConverterFactory(ScalarsConverterFactory.create()).build().create(ConnectionService.class);
        TDKService = new Builder().baseUrl("http://www.tdk.gov.tr/").addConverterFactory(ScalarsConverterFactory.create()).build().create(ConnectionService.class);
        statusesService = TwitterCore.getInstance().getApiClient().getStatusesService();
    }

    protected void getTDKPage(Node node) {
        String query = node.getName();
        Call<String> service = TDKService.getFromTDK("com_gts", "gts", query);
        StringBuilder response = new StringBuilder();

        try {// Now create matcher object.
            Matcher matcher = Pattern.compile(TDK_TERM_PATTERN).matcher(service.execute().body());
            try {
                while (matcher.find()) {
                    response.append(matcher.group()).append(' ');
                }
            } catch (IllegalStateException e) {
                response.append(' ');
                Log.e("CH-TDK", "Böyle bir sayfa yok.", e);
            }
        } catch (NullPointerException e) {
            Log.e("CH-TDK", "nullpointer", e);
        } catch (ProtocolException e) {
            Log.e("CH-TDK", "TOO MANY TDK REQUESTS, TDK SERVERS ARE HURT", e);
        } catch (IOException e) {
            Log.e("CH-TDK", "IO", e);
        }
        node.setTDKResponse(response.toString());
    }

    protected void getWikiPage(Node node, WikiAvailability test) {
        String query = node.getName();
        Call<String> request = wikiService.getFromWiki("parse", "json", "wikitext|links", "1", query);

        try {
            JSONObject obj = new JSONObject(request.execute().body());
            if (obj.has("parse")) {
                obj = obj.getJSONObject("parse");
                node.setLinks(obj.getJSONArray("links")); // json dosyasında links diye bi array tanımlanmış ondan bi dizi üretiyorum
                obj = obj.getJSONObject("wikitext");
                node.setWikiText(obj.getString("*"));
                test.setAvailable();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    //Twittera erişilerek tweetler alınıyor
    protected ArrayList<SimplifiedTweet> getTweets() {
        ArrayList<SimplifiedTweet> tweets = new ArrayList<>();
        Call<List<Tweet>> request = statusesService.homeTimeline(MAX_TWEET_COUNT, null, null, null, null, null, null);
        try {
            List<Tweet> result = request.execute().body();
            for (Tweet tweet : result) {
                tweets.add(new SimplifiedTweet(tweet));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tweets;
    }

    private interface ConnectionService {
        @GET("/index.php")
        Call<String> getFromTDK(@Query("option") String option, @Query("arama") String arama, @Query("kelime") String kelime);

        @GET("/w/api.php")
        Call<String> getFromWiki(@Query("action") String action, @Query("format") String format, @Query("prop") String prop, @Query("utf8") String utf8, @Query("page") String page);
    }
}

