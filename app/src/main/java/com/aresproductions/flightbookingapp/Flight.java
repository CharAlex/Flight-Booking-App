package com.aresproductions.flightbookingapp;

/**
 * Created by GIANNIS on 03-Jan-17.
 */

public class Flight {
    private String currency;
    private String origin;
    private String destination;
    private String airline;
    private String travelClass;
    private String price;

    public Flight(String currency, String origin, String destination, String airline, String travelClass, String price) {
        this.currency = currency;
        this.origin = origin;
        this.destination = destination;
        this.airline = airline;
        this.travelClass = travelClass;
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public String getAirline() {
        return airline;
    }

    public String getTravelClass() {
        return travelClass;
    }

    public String getPrice() {
        return price;
    }
}
