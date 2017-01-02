package com.aresproductions.flightbookingapp;

/**
 * Created by Ares on 02-Jan-17.
 */

public class Airport {
    private String label;
    private String value;

    public Airport(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }
}
