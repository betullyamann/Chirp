package com.example.betulyaman.chirp.handlers;

import android.util.Log;

import com.example.betulyaman.chirp.containers.Primitive;
import com.example.betulyaman.chirp.containers.SimplifiedTweet;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;


public class ConnectionHandler {

    protected static String getTDKPage(String query) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://www.tdk.gov.tr/").addConverterFactory(ScalarsConverterFactory.create()).build();
        ConnectionService connectionService = retrofit.create(ConnectionService.class);
        Call<String> service = connectionService.getFromTDK("com_gts", "gts", query);
        String response = "";
        try {
            String pattern = "[1-9]\\..*<br>";
            Pattern r = Pattern.compile(pattern);
            Matcher m;
            try {// Now create matcher object.
                m = r.matcher(service.execute().body());
                try {
                    while (m.find()) {
                        response += m.group() + " ";
                    }
                } catch (IllegalStateException e) {
                    System.out.println("Böyle bir sayfa yok.");
                }
            } catch (NullPointerException ne) {
                Log.d("null", ne.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    protected static Primitive getWikiPage(String query) {
        Primitive page = new Primitive(query);
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://tr.wikipedia.org/").addConverterFactory(ScalarsConverterFactory.create()).build();
        ConnectionService connectionService = retrofit.create(ConnectionService.class);
        Call<String> service = connectionService.getFromWiki("parse", "json", "wikitext|links", "1", query);

        try {
            JSONObject obj = new JSONObject(service.execute().body());
            obj = obj.getJSONObject("parse");
            page.setLinks(obj.getJSONArray("links")); // json dosyasında links diye bi array tanımlanmış ondan bi dizi üretiyorum
            obj = obj.getJSONObject("wikitext");
            page.setWikitext(obj.getString("*"));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return page;
    }

    //Twittera erişilerek tweetler alınıyor
    protected static ArrayList<SimplifiedTweet> getTweets() {
        ArrayList<SimplifiedTweet> tweets = new ArrayList<>();
        TwitterCore twitter = TwitterCore.getInstance();
        TwitterApiClient tac = twitter.getApiClient();
        StatusesService statusesService = tac.getStatusesService();
        Call<List<Tweet>> call = statusesService.homeTimeline(200, null, null, null, null, null, null);
        try {
            List<Tweet> result = call.execute().body();
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

