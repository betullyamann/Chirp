package info.chirpapp.chirp.handlers;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map.Entry;

import info.chirpapp.chirp.containers.Node;
import info.chirpapp.chirp.containers.Ontology;
import info.chirpapp.chirp.containers.WikiAvailability;

public class OntologyHandler {

    private static final Integer REFREF = 1;
    private static final Integer REFFRQ = 2;
    private static final Integer REFTRM = 3;
    private static final Integer FRQREF = 4;

    // TODO Node sayısı incelenip gerekiyorsa ekstra nodelar oluşması için işlemler eklenecek
    private static final Integer FRQFRQ = 5;
    private static final Integer FRQTRM = 6;
    private static final Integer TRMREF = 7;
    private static final Integer TRMFRQ = 8;
    private static final Integer TRMTRM = 9;
    LanguageHandler languageHandler;
    ConnectionHandler connectionHandler;
    DatabaseHandler databaseHandler;

    Context context;

    public OntologyHandler(Context context) {
        connectionHandler = new ConnectionHandler();
        languageHandler = new LanguageHandler();
        databaseHandler = new DatabaseHandler(context);
        this.context = context;
    }

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

        System.out.println(ontology.getRoot() + "\n" + "______________________________");
        for (Node node : ontology.getNodes()) {
            System.out.println(node + "\n" + "_____" +
                    "_________________________");
        }



        for (Node node : ontology.getNodes()) {
            for (String ref : ontology.getRoot().getReferences()) {
                if (node.getReferences().contains(ref)) {
                    ontology.getWords().put(ref, REFREF);
                }
                if (node.getFrequencies().containsKey(ref)) {
                    ontology.getWords().put(ref, REFFRQ);
                }
                if (node.getTerms().contains(ref)) {
                    ontology.getWords().put(ref, REFTRM);
                }
            }
            for (String frq : ontology.getRoot().getFrequencies().keySet()) {
                if (node.getReferences().contains(frq)) {
                    ontology.getWords().put(frq, FRQREF);
                }
                if (node.getFrequencies().containsKey(frq)) {
                    ontology.getWords().put(frq, FRQFRQ);
                }
                if (node.getTerms().contains(frq)) {
                    ontology.getWords().put(frq, FRQTRM);
                }
            }
            for (String trm : ontology.getRoot().getTerms()) {
                if (node.getReferences().contains(trm)) {
                    ontology.getWords().put(trm, TRMREF);
                }
                if (node.getFrequencies().containsKey(trm)) {
                    ontology.getWords().put(trm, TRMFRQ);
                }
                if (node.getTerms().contains(trm)) {
                    ontology.getWords().put(trm, TRMTRM);
                }
            }
        }

        HashMap<String, Integer> vector = vectorize(ontology);
        for (Entry<String, Integer> entry : vector.entrySet()) {
            System.out.println("key " + entry.getKey() + " value " + entry.getValue());
        }
        databaseHandler.putWholeCategory(ontology.getRoot().getName(), vector);
        Log.i("ONT", "Ontology " + ontology.getRoot().getName() + " is created.");
    }

    // Ontolojinin kelime vektörü çıkartılıyor.
    // Kelime vektöründe komşu kelimeler ve bu kelimelerin ontolojiyle olan bağlantısı tutuluyor.
    public HashMap<String, Integer> vectorize(Ontology ontology) {
        HashMap<String, Integer> vector = new HashMap<>();

        vector.put(ontology.getRoot().getName(), 10);
        vector.putAll(ontology.getWords());

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

        System.out.println(node);
        return test.isAvailable();
    }
}
