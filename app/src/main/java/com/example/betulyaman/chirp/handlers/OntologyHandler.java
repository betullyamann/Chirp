package com.example.betulyaman.chirp.handlers;

import com.example.betulyaman.chirp.containers.Node;
import com.example.betulyaman.chirp.containers.Ontology;
import com.example.betulyaman.chirp.containers.Primitive;

import java.nio.channels.Pipe;
import java.sql.Time;
import java.util.ArrayList;

public class OntologyHandler {

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

    public static ArrayList vectorize(Ontology ontology) {
        // TODO
    }

    // Wikipedia'den elde edilen kelimeler ve frekanslarla, TDK'dan elde edilen kelimelerle iki dugumun komsulugunu kontrol ediyor
    private static Integer checkAdjacency(Node root, Node leaf) {
        if (root.getPrimitive().getReferences().contains(leaf.getName()) || leaf.getPrimitive().getReferences().contains(root.getName())) {
            return 20;
        }
        if(root.getPrimitive().getFrequencyWords().contains(leaf.getName()) || leaf.getPrimitive().getFrequencyWords().contains(root.getName())) {
            return 20;
        }
        if(root.getPrimitive().getTerms().contains(leaf.getName()) || leaf.getPrimitive().getTerms().contains(root.getName())){
            return 20;
        }

        return 0;
    }


    private static void bindNodes(Node root, Node leaf, Integer strength) {
        root.addAdjacent(leaf, strength);
        leaf.addAdjacent(root, strength);
    }

    private static Primitive preparePage(String query) { // Wikipediadan gelen cevabı kopyalayıp yapıştırdım bunu kendisi alacak
        long time = System.nanoTime();

        //Wikipedia'ye sorgu yapılıp referanslar ve frekanslar alınıyor
        //TDKya sorgu yapılıp sayfa iceriği alınıyor
        Primitive page = ConnectionHandler.getWikiPage(query);

        LanguageHandler.getPageReferences(page);
        LanguageHandler.getPageWordFrequencies(page);
        LanguageHandler.getTDKWords(page, ConnectionHandler.getTDKPage(page.getName()));
    //TODO: THREAD
        time = System.nanoTime() - time;
        System.out.println("time"  + time);
        return page;
    }
}
