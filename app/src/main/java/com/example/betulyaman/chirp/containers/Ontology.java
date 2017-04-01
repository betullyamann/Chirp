package com.example.betulyaman.chirp.containers;

import java.util.ArrayList;

public class Ontology {
    private Node root;
    private ArrayList<Node> nodes;


    public Ontology(String tag) {
        root = new Node(tag);
    }

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

}
