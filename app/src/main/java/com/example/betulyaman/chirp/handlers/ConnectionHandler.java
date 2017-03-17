package com.example.betulyaman.chirp.handlers;

import com.example.betulyaman.chirp.containers.Primitive;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;


public class ConnectionHandler {

    private interface ConnectionService {
        @GET("/index.php")
        Call<String> getFromTDK(@Query("option") String option, @Query("arama") String arama, @Query("kelime") String kelime);

        @GET("/w/api.php")
        Call<String> getFromWiki(@Query("action") String action, @Query("format") String format, @Query("prop") String prop, @Query("utf8") String utf8, @Query("page") String page);
    }

    protected static String getTDKPage(String query) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://www.tdk.gov.tr/").addConverterFactory(ScalarsConverterFactory.create()).build();
        ConnectionService connectionService = retrofit.create(ConnectionService.class);
        Call<String> service = connectionService.getFromTDK("com_gts", "gts", query);
        String response = null;
        try {
            response = service.execute().body();
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
        String response;
        try {
            response = service.execute().body();
            JSONObject obj = new JSONObject(response);
            response = "";
            obj = obj.getJSONObject("parse");
            page.setLinks(obj.getJSONArray("links")); // json dosyasında links diye bi array tanımlanmış ondan bi dizi üretiyorum
            obj = obj.getJSONObject("wikitext");
            page.setWikitext(obj.getString("*"));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return page;
    }
}


