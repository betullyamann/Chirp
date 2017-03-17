package com.example.betulyaman.chirp.containers;

import java.util.ArrayList;

public class Node {
    private final String name;
    private ArrayList<Node> adjacentNodes;
    private ArrayList<Integer> bindStrength;
    private Primitive primitive;

    public Node(String name) {
        this.name = name;
        adjacentNodes = new ArrayList<>();
        bindStrength = new ArrayList<>();
    }

    public Primitive getPrimitive() {
        return primitive;
    }

    public void setPrimitive(Primitive primitive) {
        this.primitive = primitive;
    }

    public String getName() {
        return name;
    }

    public Node getAdjacent(Integer index) {
        return adjacentNodes.get(index);
    }

    public ArrayList<Node> getAdjacentNodes() {

        return adjacentNodes;
    }

    public Integer getStrength(Integer index) {
        return bindStrength.get(index);
    }

    public void addAdjacent(Node node, Integer strength) {
        adjacentNodes.add(node);
        bindStrength.add(strength);
    }

    public Integer getAdjacentCount() {
        return adjacentNodes.size();
    }

    // İncelenen düğümün belirtilen isme sahip nodunun olup olmadığını kontrol ediyor.
    public Boolean isAdjacent(String name) {
        int i = 0;
        while (!adjacentNodes.get(i).getName().equals(name) && i < adjacentNodes.size()) {
            i++;
        }
        if (i == adjacentNodes.size()) {
            return false;
        } else {
            return true;
        }
    }

}
