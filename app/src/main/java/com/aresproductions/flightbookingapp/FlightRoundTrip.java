package com.aresproductions.flightbookingapp;

import java.util.ArrayList;

/**
 * Created by Ares on 05-Feb-17.
 */

public class FlightRoundTrip extends Flight {

    private ArrayList<Trip> tripsReturn;


    public FlightRoundTrip(String currency, String origin, String destination, String airline, String travelClass, ArrayList<Trip> trips, String price, ArrayList<Trip> tripsReturn) {
        super(currency, origin, destination, airline, travelClass, trips, price);
        this.tripsReturn = tripsReturn;
    }

    public ArrayList<Trip> getTripsReturn() {
        return tripsReturn;
    }
}
