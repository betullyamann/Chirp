package com.example.betulyaman.chirp.containers;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class Primitive {
    private final String name;
    private String wikitext;
    private JSONArray links;
    private ArrayList<String> references;
    private ArrayList<FrequencyWrapper> frequencies;
    private ArrayList<String> terms;

    public Primitive(String name) {
        this.name = name;
        references = new ArrayList<>();
        frequencies = new ArrayList<>();
        terms = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getWikitext() {
        return wikitext;
    }

    public void setWikitext(String wikitext) {
        this.wikitext = wikitext;
    }

    public String getLink(int i) throws JSONException {
        return links.getJSONObject(i).getString("*");
    }

    public void setLinks(JSONArray links) {
        this.links = links;
    }

    public Integer getLinksLength(){
        return links.length();
    }

    public ArrayList<String> getReferences() {
        return references;
    }

    public void setReferences(ArrayList<String> references) {
        this.references = references;
    }

    public ArrayList<FrequencyWrapper> getFrequencies() {
        return frequencies;
    }

    public ArrayList<String> getFrequencyWords(){
        ArrayList<String> words = new ArrayList<>();
        for (FrequencyWrapper fw : frequencies) {
            words.add(fw.getWord());
        }
        return words;
    }

    public void setFrequencies(ArrayList<FrequencyWrapper> frequencies) {
        this.frequencies = frequencies;
    }

    public ArrayList<String> getTerms() {
        return terms;
    }

    public void setTerms(ArrayList<String> terms) {
        this.terms = terms;
    }
}
