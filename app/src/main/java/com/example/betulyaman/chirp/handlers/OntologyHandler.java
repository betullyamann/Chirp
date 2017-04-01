package com.example.betulyaman.chirp.handlers;

import com.example.betulyaman.chirp.containers.Node;
import com.example.betulyaman.chirp.containers.Ontology;
import com.example.betulyaman.chirp.containers.Primitive;
import com.example.betulyaman.chirp.containers.VectorElement;

import java.sql.Connection;
import java.util.ArrayList;

public class OntologyHandler {

    public static void start(){
        
    }

    // TODO Node sayısı incelenip gerekiyorsa ekstra nodelar oluşması için işlemler eklenecek

    // Tag'e iliskin ontoloji olusturuluyor
    public static void createOnthology(String tag) {
        Ontology ontology = new Ontology(tag);
        // Verilen tag'in primitive'i olusturuldu.
        ontology.getRoot().setPrimitive(preparePage(ontology.getRoot().getName()));

        for (String str : ontology.getRoot().getPrimitive().getReferences()) {
            // referanslar icin node ve bu nodelarin primitiveleri olusturuluyor.
            Node leaf = new Node(str);
            leaf.setPrimitive(preparePage(leaf.getName()));
            // tag'in referanslarla komsulugu arastiriliyor
            Integer strenght = checkAdjacency(ontology.getRoot(), leaf);
            if (strenght != 0) {
                ontology.addNode(leaf);
                bindNodes(ontology.getRoot(), leaf, strenght);
            }
        }

        // Tag'in komsularinin komsulari arastiriliyor
        for (Node branch : ontology.getRoot().getAdjacentNodes()) {
            for (String str : branch.getPrimitive().getReferences()) {
                Node leaf = new Node(str);
                leaf.setPrimitive(preparePage(leaf.getName()));
                Integer strenght = checkAdjacency(branch, leaf);
                if (strenght != 0) {
                    ontology.addNode(leaf);
                    bindNodes(branch, leaf, strenght);
                }
            }
        }
    }

    // Ontolojinin kelime vektörü çıkartılıyor.
    // Kelime vektöründe komşu kelimeler ve bu kelimelerin ontolojiyle olan bağlantısı tutuluyor.
    public static ArrayList<VectorElement> vectorize(Ontology ontology) {
        ArrayList<VectorElement> vector = new ArrayList<>();

        vector.add(new VectorElement(ontology.getRoot().getName(), 40));
        //TODO 40?

        for (int i = 0; i < ontology.getRoot().getAdjacentCount(); i++) {
            vector.add(new VectorElement(ontology.getRoot().getAdjacent(i).getName(), ontology.getRoot().getStrength(i)));
        }

        for (int i = 0; i < ontology.getRoot().getAdjacentCount(); i++) {
            Node node = ontology.getRoot().getAdjacent(i);
            for (int j = 0; j < node.getAdjacentCount(); j++) {
                for (VectorElement element : vector) {
                    if (!node.getAdjacent(j).getName().equals(element.getWord())) {
                        vector.add(new VectorElement(node.getAdjacent(j).getName(), node.getStrength(i)));
                    } else {
                        if (element.getFrequency() < node.getStrength(j)) {
                            element.setFrequency(node.getStrength(j));
                        }
                    }
                }
            }
        }


    }


    // Wikipedia'den elde edilen kelimeler ve frekanslarla, TDK'dan elde edilen kelimelerle iki dugumun komsulugunu kontrol ediyor
    private static Integer checkAdjacency(Node root, Node leaf) {
        if (root.getPrimitive().getReferences().contains(leaf.getName()) || leaf.getPrimitive().getReferences().contains(root.getName())) {
            return 20;
        }
        if (root.getPrimitive().getFrequencyWords().contains(leaf.getName()) || leaf.getPrimitive().getFrequencyWords().contains(root.getName())) {
            return 20;
        }
        if (root.getPrimitive().getTerms().contains(leaf.getName()) || leaf.getPrimitive().getTerms().contains(root.getName())) {
            return 20;
        }

        return 0;
    }


    private static void bindNodes(Node root, Node leaf, Integer strength) {
        root.addAdjacent(leaf, strength);
        leaf.addAdjacent(root, strength);
    }

    public static Primitive preparePage(String query) { // Wikipediadan gelen cevabı kopyalayıp yapıştırdım bunu kendisi alacak

        //Wikipedia'ye sorgu yapılıp referanslar ve frekanslar alınıyor
        //TDKya sorgu yapılıp sayfa iceriği alınıyor
        long t = System.nanoTime();
        final Primitive page = ConnectionHandler.getWikiPage(query);


        Thread frequenciesThread = new Thread() {
            public void run() {
                long time = System.nanoTime();
                LanguageHandler.getPageWordFrequencies(page);
                System.out.println("FREQUENCIES TIME " + (System.nanoTime() - time));
            }
        };
        frequenciesThread.start();

        Thread referencesThread = new Thread() {
            public void run() {
                long time = System.nanoTime();
                LanguageHandler.getPageReferences(page);
                System.out.println("REFERENCES " + (System.nanoTime() - time));
            }
        };
        referencesThread.start();

        long time = System.nanoTime();
        LanguageHandler.getTDKWords(page, ConnectionHandler.getTDKPage(page.getName()));
        System.out.println("TDKWORDS TIME " + (System.nanoTime() - time));

        try {
            long tt = System.nanoTime();
            referencesThread.join();
            frequenciesThread.join();
            System.out.println("JOIN " + (System.nanoTime() - tt));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("time " + (System.nanoTime() - t));
        return page;
    }
}
