package info.chirpapp.chirp.handlers;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import info.chirpapp.chirp.containers.Node;
import info.chirpapp.chirp.containers.Ontology;
import info.chirpapp.chirp.containers.WikiAvailability;

public class OntologyHandler {

    LanguageHandler languageHandler;
    ConnectionHandler connectionHandler;
    DatabaseHandler databaseHandler;

    public OntologyHandler(Context context) {
        connectionHandler = new ConnectionHandler();
        languageHandler = new LanguageHandler();
        databaseHandler = new DatabaseHandler(context);
    }

    // TODO Node sayısı incelenip gerekiyorsa ekstra nodelar oluşması için işlemler eklenecek

    // Tag'e iliskin ontoloji olusturuluyor
    public void createOnthology(String tag) {
        Ontology ontology = new Ontology(tag);
        // Verilen tag'in primitive'i olusturuldu.
        prepareNode(ontology.getRoot());

        for (String str : ontology.getRoot().getReferences()) {
            // referanslar icin node ve bu nodelarin primitiveleri olusturuluyor.
            Node leaf = new Node(str);
            Log.i("ONT", "========================================");
            Log.i("ONT", "LEAF NAME " + leaf.getName());
            if (prepareNode(leaf)) {
                ontology.addNode(leaf);
            } else {
                Log.w("ONT", "PAGE " + leaf.getName() + " ISNT AVAILABLE ON WIKIPEDIA");
            }
        }


        // TODO BURAYA BAK Bİ HELE

        databaseHandler.putWholeCategory(ontology.getRoot().getName(), vectorize(ontology));
        Log.i("ONT", "Ontology " + ontology.getRoot().getName() + " is created.");
    }

    // Ontolojinin kelime vektörü çıkartılıyor.
    // Kelime vektöründe komşu kelimeler ve bu kelimelerin ontolojiyle olan bağlantısı tutuluyor.
    public HashMap<String, Integer> vectorize(Ontology ontology) {
        HashMap<String, Integer> vector = new HashMap<>();

        vector.put(ontology.getRoot().getName(), 40);
        //TODO REWRITE VECTORIZE


        return vector;
    }


    public Boolean prepareNode(Node node) { // Wikipediadan gelen cevabı kopyalayıp yapıştırdım bunu kendisi alacak

        //Wikipedia'ye sorgu yapılıp referanslar ve frekanslar alınıyor
        //TDKya sorgu yapılıp sayfa iceriği alınıyor
        long wTime = System.nanoTime();
        WikiAvailability test = new WikiAvailability();
        Thread wikipediaThread = new Thread(() -> connectionHandler.getWikiPage(node, test));
        wikipediaThread.start();

        long tTime = System.nanoTime();
        Thread TDKThread = new Thread(() -> connectionHandler.getTDKPage(node));
        TDKThread.start();

        try {
            wikipediaThread.join();
            TDKThread.join();
            Log.i("WKI", "DONE IN " + (float) (System.nanoTime() - wTime) / 1000000L);
            Log.i("TDK", "DONE IN " + (float) ((System.nanoTime() - tTime) / 1000000L));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (test.isAvailable()) {
            long time = System.nanoTime();
            languageHandler.parseReferences(node);
            Log.i("REF", "DONE IN " + (float) ((System.nanoTime() - time) / 1000000L));

            time = System.nanoTime();
            languageHandler.parseFrequencies(node);
            Log.i("FRE", "DONE IN " + (float) ((System.nanoTime() - time) / 1000000L));

            time = System.nanoTime();
            languageHandler.parseTerms(node);
            Log.i("TERMS", "DONE IN " + (float) ((System.nanoTime() - time) / 1000000L));
        }

        return test.isAvailable();
    }
}
