package com.aresproductions.flightbookingapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;


/**
 * Created by GIANNIS on 03-Jan-17.
 */

public class Flight implements Parcelable{
    private String currency;
    private String origin;
    private String destination;
    private String airline;
    private String travelClass;
    private ArrayList trips;
    private int numberOfTrips;


    private String price;

    public Flight(String currency, String origin, String destination, String airline, String travelClass, ArrayList<Trip> trips, String price) {
        this.currency = currency;
        this.origin = origin;
        this.destination = destination;
        this.airline = airline;
        this.travelClass = travelClass;
        this.trips = trips;
        this.price = price;
        this.numberOfTrips = trips.size();
    }

    public ArrayList<Trip> getTrips() {
        return trips;
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

    public static final Creator<Flight> CREATOR = new Creator<Flight>() {
        @Override
        public Flight createFromParcel(Parcel in) {
            return new Flight(in);
        }

        @Override
        public Flight[] newArray(int size) {
            return new Flight[size];
        }
    };



    protected Flight(Parcel in) {
        currency = in.readString();
        origin = in.readString();
        destination = in.readString();
        airline = in.readString();
        travelClass = in.readString();
        price = in.readString();
        trips = in.readArrayList(Trip.class.getClassLoader());

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(currency);
        parcel.writeString(origin);
        parcel.writeString(destination);
        parcel.writeString(airline);
        parcel.writeString(travelClass);
        parcel.writeString(price);
        parcel.writeList(trips);


    }
}
