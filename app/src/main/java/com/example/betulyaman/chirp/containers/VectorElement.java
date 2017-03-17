package com.example.betulyaman.chirp.containers;

public class VectorElement {
    private String word;
    private Integer point;

    public VectorElement(String word, Integer point) {
        this.word = word;
        this.point = point;
    }

    public Integer getFrequency() {
        return point;
    }

    public void setFrequency(Integer frequency) {
        this.point = frequency;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
