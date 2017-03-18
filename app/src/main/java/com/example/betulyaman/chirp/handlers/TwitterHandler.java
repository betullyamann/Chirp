package com.example.betulyaman.chirp.handlers;

import android.app.Activity;
import android.content.Intent;

import com.example.betulyaman.chirp.LoginActivity;
import com.example.betulyaman.chirp.containers.SimplifiedTweet;

import net.zemberek.araclar.turkce.YaziBirimi;
import net.zemberek.araclar.turkce.YaziBirimiTipi;
import net.zemberek.araclar.turkce.YaziIsleyici;
import net.zemberek.erisim.Zemberek;
import net.zemberek.tr.yapi.TurkiyeTurkcesi;
import net.zemberek.yapi.KelimeTipi;

import java.util.ArrayList;
import java.util.List;

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

    public static void prepareTweet(ArrayList<SimplifiedTweet> tweets) {

        Zemberek zemberek = new Zemberek(new TurkiyeTurkcesi());

        for (SimplifiedTweet tweet : tweets) {
            List<YaziBirimi> analizDizisi = YaziIsleyici.analizDizisiOlustur(tweet.getText());
            for (int j = 0; j < analizDizisi.size(); j++) {
                try {
                    if (analizDizisi.get(j).tip == YaziBirimiTipi.KELIME) {

                        if (zemberek.kelimeCozumle(analizDizisi.get(j).icerik)[0].kok().tip() == KelimeTipi.ISIM) {
                            tweet.addWord(zemberek.kelimeCozumle(analizDizisi.get(j).icerik)[0].kok().icerik());
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    // Zemberek'in cozumleyemedigi kelimeler catch'e dusuyor
                }
            }
        }

        for (SimplifiedTweet t : tweets) {
            System.out.println("ParsedTweet: " + t.getParsedText());
        }
    }
}
