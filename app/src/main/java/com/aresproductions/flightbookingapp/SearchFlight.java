package com.aresproductions.flightbookingapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ares on 04-Jan-17.
 */

public class SearchFlight implements Parcelable{
    private String origin;
    private String destination;
    private String departure;
    private String returnDate;
    private String adults;
    private String children;
    private String infants;
    private String travel_class;
    private String oneWay;

    public SearchFlight(String origin, String destination, String departure, String returnDate, String adults, String children, String infants, String travel_class, String oneWay) {
        this.origin = origin;
        this.destination = destination;
        this.departure = departure;
        this.returnDate = returnDate;
        this.adults = adults;
        this.children = children;
        this.infants = infants;
        this.travel_class = travel_class;
        this.oneWay = oneWay;
    }


    protected SearchFlight(Parcel in) {
        origin = in.readString();
        destination = in.readString();
        departure = in.readString();
        returnDate = in.readString();
        adults = in.readString();
        children = in.readString();
        infants = in.readString();
        travel_class = in.readString();
        oneWay = in.readString();
    }

    public static final Creator<SearchFlight> CREATOR = new Creator<SearchFlight>() {
        @Override
        public SearchFlight createFromParcel(Parcel in) {
            return new SearchFlight(in);
        }

        @Override
        public SearchFlight[] newArray(int size) {
            return new SearchFlight[size];
        }
    };

    public String getOrigin() {

        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public String getDeparture() {
        return departure;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public String getAdults() {
        return adults;
    }

    public String getChildren() {
        return children;
    }

    public String getInfants() {
        return infants;
    }

    public String getTravel_class() {
        return travel_class;
    }

    public String getOneWay() {
        return oneWay;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(origin);
        parcel.writeString(destination);
        parcel.writeString(departure);
        parcel.writeString(returnDate);
        parcel.writeString(adults);
        parcel.writeString(children);
        parcel.writeString(infants);
        parcel.writeString(travel_class);
        parcel.writeString(oneWay);


    }
}
