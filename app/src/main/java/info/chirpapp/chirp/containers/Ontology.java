package info.chirpapp.chirp.containers;

import java.util.ArrayList;
import java.util.HashMap;

public class Ontology {
    private final Node root;
    private final ArrayList<Node> nodes;
    private final HashMap<String, Integer> words;


    public Ontology(String tag) {
        root = new Node(tag);
        nodes = new ArrayList<>();
        words = new HashMap<>();
    }

    public Node getRoot() {
        return root;
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public HashMap<String, Integer> getWords() {
        return words;
    }
}
