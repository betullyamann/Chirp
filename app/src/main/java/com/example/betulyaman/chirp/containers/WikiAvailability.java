package com.example.betulyaman.chirp.containers;

public class WikiAvailability {
    private Boolean available;

    public WikiAvailability() {
        available = false;
    }

    public void setAvailable() {
        available = true;
    }

    public Boolean isAvailable() {
        return available;
    }
}
