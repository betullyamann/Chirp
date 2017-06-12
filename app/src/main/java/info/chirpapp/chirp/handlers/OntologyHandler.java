package info.chirpapp.chirp.handlers;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Objects;

import info.chirpapp.chirp.containers.Node;
import info.chirpapp.chirp.containers.Ontology;
import info.chirpapp.chirp.containers.WikiAvailability;

public class OntologyHandler {

    private static final Integer TRM_PTS = 1;
    private static final Integer FRQ_PTS = 1;
    private static final Integer REF_PTS = 5;

    private final LanguageHandler languageHandler;
    private final ConnectionHandler connectionHandler;
    private final DatabaseHandler databaseHandler;

    Context context;

    public OntologyHandler(Context context) {
        connectionHandler = new ConnectionHandler();
        languageHandler = new LanguageHandler();
        databaseHandler = new DatabaseHandler(context);
        this.context = context;
    }

    // Tag'e iliskin ontoloji olusturuluyor
    public void createOntology(String tag) {
        Ontology ontology = new Ontology(tag);
        // Verilen tag'in primitive'i olusturuldu.
        prepareNode(ontology.getRoot());

        for (String str : ontology.getRoot().getReferences()) {
            // referanslar icin node ve bu nodelarin primitiveleri olusturuluyor.
            Node leaf = new Node(str);
            //Log.i("ONT", "========================================");
            Log.i("ONT", "LEAF NAME " + leaf.getName());
            if (!Objects.equals(str, ontology.getRoot().getName())) {
                if (prepareNode(leaf)) {
                    ontology.addNode(leaf);
                } else {
                    Log.w("ONT", "PAGE " + leaf.getName() + " ISNT AVAILABLE ON WIKIPEDIA");
                }
            }
        }

        /*System.out.println(ontology.getRoot() + "\n" + "______________________________");
        for (Node node : ontology.getNodes()) {
            System.out.println(node + "\n" + "______________________________");
        }*/

        HashMap<String, Integer> pagePoints = new HashMap<>();
        for (Node node : ontology.getNodes()) {
            pagePoints.put(node.getName(), 0);
        }

        ArrayList<String> wordPoints = new ArrayList<>();
        for (Node node : ontology.getNodes()) {
            for (String str : node.getReferences()) {
                if (!wordPoints.contains(str)) {
                    wordPoints.add(str);
                }
            }
            for (String str : node.getFrequencies().keySet()) {
                if (!wordPoints.contains(str)) {
                    wordPoints.add(str);
                }
            }
            for (String str : node.getTerms()) {
                if (!wordPoints.contains(str)) {
                    wordPoints.add(str);
                }
            }
        }

        //Puan hesaplama
        for (Node node : ontology.getNodes()) {
            for (String str : node.getReferences()) {
                if (ontology.getRoot().getReferences().contains(str)) {
                    pagePoints.put(node.getName(), pagePoints.get(node.getName()) + REF_PTS);
                }
                if (ontology.getRoot().getFrequencies().keySet().contains(str)) {
                    pagePoints.put(node.getName(), pagePoints.get(node.getName()) + FRQ_PTS);
                }
                if (ontology.getRoot().getTerms().contains(str)) {
                    pagePoints.put(node.getName(), pagePoints.get(node.getName()) + TRM_PTS);
                }
            }
            for (String str : node.getFrequencies().keySet()) {
                if (ontology.getRoot().getReferences().contains(str)) {
                    pagePoints.put(node.getName(), pagePoints.get(node.getName()) + REF_PTS);
                }
                if (ontology.getRoot().getFrequencies().keySet().contains(str)) {
                    pagePoints.put(node.getName(), pagePoints.get(node.getName()) + FRQ_PTS);
                }
                if (ontology.getRoot().getTerms().contains(str)) {
                    pagePoints.put(node.getName(), pagePoints.get(node.getName()) + TRM_PTS);
                }
            }
            for (String str : node.getTerms()) {
                if (ontology.getRoot().getReferences().contains(str)) {
                    pagePoints.put(node.getName(), pagePoints.get(node.getName()) + REF_PTS);
                }
                if (ontology.getRoot().getFrequencies().keySet().contains(str)) {
                    pagePoints.put(node.getName(), pagePoints.get(node.getName()) + FRQ_PTS);
                }
                if (ontology.getRoot().getTerms().contains(str)) {
                    pagePoints.put(node.getName(), pagePoints.get(node.getName()) + TRM_PTS);
                }
            }
        }

        //Normalizasyon
        for (Entry<String, Integer> entry : pagePoints.entrySet()) {
            if (!wordPoints.isEmpty()) {
                entry.setValue((entry.getValue() * 1000) / wordPoints.size());
            } else {
                entry.setValue(0);
            }
        }

        /*
        HashMap<String, Integer> vector = vectorize(ontology);
        for (Entry<String, Integer> entry : vector.entrySet()) {
            System.out.println("key " + entry.getKey() + " value " + entry.getValue());
        }*/
        if (!ontology.getNodes().isEmpty()) {
            if (databaseHandler.putWholeCategory(ontology.getRoot().getName(), pagePoints)) {
                Log.i("ONT", "Ontology " + ontology.getRoot().getName() + " is created.");
            } else {
                Log.i("ONT", "Ontology " + ontology.getRoot().getName() + " already exist");
            }
        } else {
            Log.e("ONT", "Wikipedia sayfasına erişilemedi.");
        }
    }

    public Boolean prepareNode(Node node) {

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
            //Log.i("WKI", "DONE IN " + (float) (System.nanoTime() - wTime) / 1000000L);
            //Log.i("TDK", "DONE IN " + (float) ((System.nanoTime() - tTime) / 1000000L));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (test.isAvailable()) {
            long time = System.nanoTime();
            languageHandler.parseReferences(node);
            //Log.i("REF", "DONE IN " + (float) ((System.nanoTime() - time) / 1000000L));

            time = System.nanoTime();
            languageHandler.parseFrequencies(node);
            //Log.i("FRE", "DONE IN " + (float) ((System.nanoTime() - time) / 1000000L));

            time = System.nanoTime();
            languageHandler.parseTerms(node);
            //Log.i("TERMS", "DONE IN " + (float) ((System.nanoTime() - time) / 1000000L));
        }

        //System.out.println(node);
        return test.isAvailable();
    }
}
