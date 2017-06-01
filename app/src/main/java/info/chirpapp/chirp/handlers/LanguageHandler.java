package info.chirpapp.chirp.handlers;

import net.zemberek.araclar.turkce.YaziBirimi;
import net.zemberek.araclar.turkce.YaziBirimiTipi;
import net.zemberek.araclar.turkce.YaziIsleyici;
import net.zemberek.erisim.Zemberek;
import net.zemberek.tr.yapi.TurkiyeTurkcesi;
import net.zemberek.yapi.Kelime;
import net.zemberek.yapi.KelimeTipi;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import info.chirpapp.chirp.containers.Node;
import info.chirpapp.chirp.containers.SimplifiedTweet;


public class LanguageHandler {

    private static final Integer FREQUENCY_THRESHOLD = 5;
    private static final ArrayList<String> STOP_WORDS = new ArrayList<>(Arrays.asList("a", "acaba", "altı", "altmış", "ama", "ancak",
            "arada", "artık", "asla", "aslında", "aslında", "ayrıca", "az", "bana", "bazen", "bazı", "bazıları", "belki", "ben",
            "benden", "beni", "benim", "beri", "beş", "bile", "bilhassa", "bin", "bir", "biraz", "birçoğu", "birçok", "biri", "birisi",
            "birkaç", "birşey", "biz", "bizden", "bize", "bizi", "bizim", "böyle", "böylece", "bu", "buna", "bunda", "bundan", "bunlar",
            "bunları", "bunların", "bunu", "bunun", "burada", "bütün", "çoğu", "çoğunu", "çok", "çünkü", "da", "daha", "dahi", "dan",
            "de", "defa", "değil", "diğer", "diğeri", "diğerleri", "diye", "doksan", "dokuz", "dolayı", "dolayısıyla", "dört", "e",
            "edecek", "eden", "ederek", "edilecek", "ediliyor", "edilmesi", "ediyor", "eğer", "elbette", "elli", "en", "etmesi", "etti",
            "ettiği", "ettiğini", "fakat", "falan", "filan", "gene", "gereği", "gerek", "gibi", "göre", "hala", "halde", "halen", "hangi",
            "hangisi", "hani", "hatta", "hem", "henüz", "hep", "hepsi", "her", "herhangi", "herkes", "herkese", "herkesi", "herkesin",
            "hiç", "hiçbir", "hiçbiri", "i", "ı", "için", "içinde", "iki", "ile", "ilgili", "ise", "işte", "itibaren", "itibariyle",
            "kaç", "kadar", "karşın", "kendi", "kendilerine", "kendine", "kendini", "kendisi", "kendisine", "kendisini", "kez", "ki",
            "kim", "kime", "kimi", "kimin", "kimisi", "kimse", "kırk", "madem", "mi", "mı", "milyar", "milyon", "mu", "mü", "nasıl",
            "ne", "neden", "nedenle", "nerde", "nerede", "nereye", "neyse", "niçin", "nin", "nın", "niye", "nun", "nün", "o", "öbür",
            "olan", "olarak", "oldu", "olduğu", "olduğunu", "olduklarını", "olmadı", "olmadığı", "olmak", "olması", "olmayan", "olmaz",
            "olsa", "olsun", "olup", "olur", "olur", "olursa", "oluyor", "on", "ön", "ona", "önce", "ondan", "onlar", "onlara", "onlardan",
            "onları", "onların", "onu", "onun", "orada", "öte", "ötürü", "otuz", "öyle", "oysa", "pek", "rağmen", "sana", "sanki", "sanki",
            "şayet", "şekilde", "sekiz", "seksen", "sen", "senden", "seni", "senin", "şey", "şeyden", "şeye", "şeyi", "şeyler", "şimdi",
            "siz", "siz", "sizden", "sizden", "size", "sizi", "sizi", "sizin", "sizin", "sonra", "şöyle", "şu", "şuna", "şunları",
            "şunu", "ta", "tabii", "tam", "tamam", "tamamen", "tarafından", "trilyon", "tüm", "tümü", "u", "ü", "üç", "un", "ün", "üzere",
            "var", "vardı", "ve", "veya", "ya", "ya da", "yani", "yapacak", "yapılan", "yapılması", "yapıyor", "yapmak", "yaptı", "yaptığı",
            "yaptığını", "yaptıkları", "ye", "yedi", "yerine", "yetmiş", "yi", "yı", "yine", "yirmi", "yoksa", "yu", "yüz", "zaten", "zira"));
    private final Zemberek zemberek;

    public LanguageHandler() {
        zemberek = new Zemberek(new TurkiyeTurkcesi());
    }

    public void parseTweets(ArrayList<SimplifiedTweet> tweets) {

        for (SimplifiedTweet tweet : tweets) {
            List<YaziBirimi> analizDizisi = YaziIsleyici.analizDizisiOlustur(tweet.getText());
            for (YaziBirimi birim : analizDizisi) {
                try {
                    if (birim.tip == YaziBirimiTipi.KELIME) {
                        Kelime[] kelimeDizisi = zemberek.kelimeCozumle(birim.icerik);
                        if (Objects.equals(kelimeDizisi[0].kok().tip(), KelimeTipi.ISIM) && !tweet.getWords().contains(kelimeDizisi[0].kok().icerik())) {
                            tweet.addWord(kelimeDizisi[0].kok().icerik());
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    // Zemberek'in cozumleyemedigi kelimeler catch'e dusuyor
                }
            }
            tweet.getWords().removeAll(STOP_WORDS);
        }
    }

    //TDK'dan dönen sayfayı temizleyerek isimleri words dizisine atıyor.
    public void parseTerms(Node node) {
        String not_found_pattern = "  sözü bulunamadı.";

        List<YaziBirimi> analizDizisi = YaziIsleyici.analizDizisiOlustur(node.getTDKResponse());
        for (YaziBirimi birim : analizDizisi) {
            if (birim.tip == YaziBirimiTipi.KELIME) {
                Kelime[] kelimeDizisi = zemberek.kelimeCozumle(birim.icerik);
                try {
                    if (Objects.equals(kelimeDizisi[0].kok().tip(), KelimeTipi.ISIM) && !node.getTerms().contains(kelimeDizisi[0].kok().icerik())) {
                        node.getTerms().add(kelimeDizisi[0].kok().icerik());
                    }
                } catch (IndexOutOfBoundsException e) {
                    // Zemberek'in cozumleyemedigi kelimeler catch'e dusuyor
                }
            }
        }

        node.getTerms().removeAll(STOP_WORDS);
        node.setTDKResponse("");
    }

    //Wikipedia'dan gelen referansları temizleyerek isim olanları references dizisini atıyor.
    public void parseReferences(Node node) {
        try {
            StringBuilder temp = new StringBuilder();
            for (int i = 0; i < node.getLinksLength(); i++) {
                temp.append(node.getLinks().get(i)).append(" ");
            }
            List<YaziBirimi> analizDizisi = YaziIsleyici.analizDizisiOlustur(temp.toString());
            temp = null;
            for (YaziBirimi birim : analizDizisi) {
                try {
                    if (birim.tip == YaziBirimiTipi.KELIME) {
                        Kelime[] kelimeDizisi = zemberek.kelimeCozumle(birim.icerik);
                        if (Objects.equals(KelimeTipi.ISIM, kelimeDizisi[0].kok().tip()) && !node.getReferences().contains(kelimeDizisi[0].kok().icerik())) {
                            node.getReferences().add(kelimeDizisi[0].kok().icerik());
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    // Zemberek'in cozumleyemedigi kelimeler catch'e dusuyor
                }
            }

            node.getReferences().removeAll(STOP_WORDS);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Donen cevaplar islendikten sonra siliniyor. RAM tasarrufu.
        node.setLinks(new JSONArray());
    }

    // Kelimelerin frekanslari bulunuyor.
    public void parseFrequencies(Node node) {
        List<YaziBirimi> analizDizisi = YaziIsleyici.analizDizisiOlustur(node.getWikiText());
        for (YaziBirimi birim : analizDizisi) {
            try {
                if (birim.tip == YaziBirimiTipi.KELIME) {
                    Kelime[] kelimeDizisi = zemberek.kelimeCozumle(birim.icerik);
                    if (Objects.equals(kelimeDizisi[0].kok().tip(), KelimeTipi.ISIM) || !STOP_WORDS.contains(kelimeDizisi[0].kok().icerik())) {
                        node.updateFrequencies(kelimeDizisi[0].kok().icerik());
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                // Zemberek'in cozumleyemedigi kelimeler catch'e dusuyor
            }
        }

        Iterator<Entry<String, Integer>> iterator = node.getFrequencies().entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<String, Integer> entry = iterator.next();
            if (entry.getValue() < FREQUENCY_THRESHOLD || STOP_WORDS.contains(entry.getKey())) {
                iterator.remove();
            }
        }

        node.setWikiText("");
    }
}



