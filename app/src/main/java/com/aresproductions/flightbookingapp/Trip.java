package com.aresproductions.flightbookingapp;

import android.os.Parcel;
import android.os.Parcelable;

public class Trip implements Parcelable {
    private String departs_at;
    private String arrives_at;
    private String depart_airport;
    private String arrival_airport;


    public Trip(String departs_at, String arrives_at, String depart_airport, String arrival_airport) {
        this.departs_at = departs_at;
        this.arrives_at = arrives_at;
        this.depart_airport = depart_airport;
        this.arrival_airport = arrival_airport;
    }

    public String getDeparts_at() {
        return departs_at;
    }

    public String getArrives_at() {
        return arrives_at;
    }

    public String getDepart_airport() {
        return depart_airport;
    }

    public String getArrival_airport() {
        return arrival_airport;
    }


    public static final Creator<Trip> CREATOR = new Creator<Trip>() {
        @Override
        public Trip createFromParcel(Parcel in) {
            return new Trip(in);
        }

        @Override
        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };



    protected Trip(Parcel in) {
        departs_at = in.readString();
        arrives_at = in.readString();
        depart_airport = in.readString();
        arrival_airport = in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(departs_at);
        parcel.writeString(arrives_at);
        parcel.writeString(depart_airport);
        parcel.writeString(arrival_airport);

    }


}
