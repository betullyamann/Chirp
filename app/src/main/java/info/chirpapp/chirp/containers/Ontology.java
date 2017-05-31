package info.chirpapp.chirp.containers;

import java.util.ArrayList;

public class Ontology {
    private final Node root;
    private final ArrayList<Node> nodes;


    public Ontology(String tag) {
        root = new Node(tag);
        nodes = new ArrayList<>();
    }

    public Node getRoot() {
        return root;
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

}
