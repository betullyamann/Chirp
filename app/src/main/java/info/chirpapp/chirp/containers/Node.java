package info.chirpapp.chirp.containers;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

public class Node {
    private final String name;
    private String wikiText;
    private JSONArray links;
    private String TDKResponse;
    private ArrayList<String> references;
    private HashMap<String, Integer> frequencies;
    private ArrayList<String> terms;

    public Node(String name) {
        this.name = name;
        references = new ArrayList<>();
        frequencies = new HashMap<>();
        terms = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getWikiText() {
        return wikiText;
    }

    public void setWikiText(String wikiText) {
        this.wikiText = wikiText;
    }

    public JSONArray getLinks() throws JSONException {
        return links;
    }

    public void setLinks(JSONArray links) {
        this.links = links;
    }

    public String getTDKResponse() {
        return TDKResponse;
    }

    public void setTDKResponse(String TDKResponse) {
        this.TDKResponse = TDKResponse;
    }

    public Integer getLinksLength() {
        return links.length();
    }

    public ArrayList<String> getReferences() {
        return references;
    }

    public void setReferences(ArrayList<String> references) {
        this.references = references;
    }

    public HashMap<String, Integer> getFrequencies() {
        return frequencies;
    }

    public void setFrequencies(HashMap<String, Integer> frequencies) {
        this.frequencies = frequencies;
    }

    public Boolean updateFrequencies(String word) {
        if (frequencies.containsKey(word)) {
            Integer temp = frequencies.get(word);
            frequencies.remove(word, temp);
            frequencies.put(word, temp + 1);
            return true;
        } else {
            frequencies.put(word, 1);
            return false;
        }
    }

    public ArrayList<String> getTerms() {
        return terms;
    }

    public void setTerms(ArrayList<String> terms) {
        this.terms = terms;
    }

}
