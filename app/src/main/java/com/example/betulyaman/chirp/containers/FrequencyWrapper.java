package com.example.betulyaman.chirp.containers;

public class FrequencyWrapper
{
    private Integer frequency;
    private String word;

    public FrequencyWrapper(String word){
        frequency = 1;
        this.word = word;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void incrementFrequency(){
        frequency = frequency + 1;
    }
}
