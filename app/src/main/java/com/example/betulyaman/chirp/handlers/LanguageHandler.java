package com.example.betulyaman.chirp.handlers;

import android.util.Log;

import com.example.betulyaman.chirp.containers.FrequencyWrapper;
import com.example.betulyaman.chirp.containers.Primitive;
import com.example.betulyaman.chirp.containers.SimplifiedTweet;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class LanguageHandler {

    private static final int FREQUENCY_THRESHOLD = 2;

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
            Log.i("ParsedTweet", t.getText());
        }
    }

    //TDK'dan dönen sayfayı temizleyerek isimleri words dizisine atıyor.
    public static void getTDKWords(Primitive page, String text) {
        String not_found_pattern = "  sözü bulunamadı.";
        Zemberek zemberek = new Zemberek(new TurkiyeTurkcesi());

        //System.out.println(stext);

        List<YaziBirimi> analizDizisi = YaziIsleyici.analizDizisiOlustur(text);
        for (int i = 0; i < analizDizisi.size(); i++) {
            if (analizDizisi.get(i).tip == YaziBirimiTipi.KELIME) {
                try {
                    if (zemberek.kelimeCozumle(analizDizisi.get(i).icerik)[0].kok().tip() == KelimeTipi.ISIM) {
                        page.getTerms().add(zemberek.kelimeCozumle(analizDizisi.get(i).icerik)[0].kok().icerik());
                    }
                } catch (IndexOutOfBoundsException e) {
                    // Zemberek'in cozumleyemedigi kelimeler catch'e dusuyor
                }
            }
        }

        for (String s : page.getTerms()) {
            Log.i("TDK", s);
        }

    }

    //Wikipedia'dan gelen referansları temizleyerek isim olanları references dizisini atıyor.
    public static void getPageReferences(Primitive page) {

        Zemberek zemberek = new Zemberek(new TurkiyeTurkcesi());

        try {
            for (int i = 0; i < page.getLinksLength(); i++) {
                List<YaziBirimi> analizDizisi = YaziIsleyici.analizDizisiOlustur(page.getLink(i));
                for (int j = 0; j < analizDizisi.size(); j++) {
                    try {
                        if (analizDizisi.get(j).tip == YaziBirimiTipi.KELIME) {

                            if (zemberek.kelimeCozumle(analizDizisi.get(j).icerik)[0].kok().tip() == KelimeTipi.ISIM) {
                                page.getReferences().add(zemberek.kelimeCozumle(analizDizisi.get(j).icerik)[0].kok().icerik());
                            }
                        }
                    } catch (IndexOutOfBoundsException e) {
                        // Zemberek'in cozumleyemedigi kelimeler catch'e dusuyor
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Donen cevaplar islendikten sonra siliniyor. RAM tasarrufu.
        page.setLinks(null);

        for (int i = 0; i < page.getReferences().size(); i++) {
            Log.i("referans", page.getReferences().get(i));
        }
    }

    // Kelimelerin frekanslari bulunuyor.
    public static void getPageWordFrequencies(Primitive page) {

        Zemberek zemberek = new Zemberek(new TurkiyeTurkcesi());
        ArrayList<FrequencyWrapper> frequencies = new ArrayList<>();
        frequencies.add(new FrequencyWrapper("*"));

        List<YaziBirimi> analizDizisi = YaziIsleyici.analizDizisiOlustur(page.getWikitext());
        for (int i = 0; i < analizDizisi.size(); i++) {
            try {
                if (analizDizisi.get(i).tip == YaziBirimiTipi.KELIME) {
                    if (zemberek.kelimeCozumle(analizDizisi.get(i).icerik)[0].kok().tip() == KelimeTipi.ISIM) {
                        //Bu key daha önce eklendi mi diye kontrol ediyor.
                        String str = zemberek.kelimeCozumle(analizDizisi.get(i).icerik)[0].kok().icerik();
                        int j = 0;
                        int index = -1;
                        //System.out.println(str);
                        while (j < frequencies.size()) {
                            if (str.equals(frequencies.get(j).getWord())) {
                                index = j;
                            }
                            // System.out.println("WHILE " + j);
                            j++;
                            // System.out.println("ELELELE " + j);
                        }
                        //System.out.println("!!!!!!! " + j);
                        if (index == -1) {
                            //System.out.println("THEN !!!!!!!!!!");
                            frequencies.add(new FrequencyWrapper(str));
                        } else {
                            //System.out.println("ELSE !!!!!!!!!");
                            frequencies.get(index).incrementFrequency();
                        }
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                // Zemberek'in cozumleyemedigi kelimeler catch'e dusuyor
            }

        }

        int i = 0;
        //Frekans değeri esik degerden kucuk olanlar siliniyor
        while (i < frequencies.size()) {
            if (frequencies.get(i).getFrequency() <= FREQUENCY_THRESHOLD) {
                frequencies.remove(i);
            } else {
                i++;
            }
        }
        //System.out.println(frequencies.size());
        for (i = 0; i < frequencies.size(); i++) {
            Log.i("word-freq", frequencies.get(i).getWord() + " - " + frequencies.get(i).getFrequency());
        }
        page.setFrequencies(frequencies);
    }


}



