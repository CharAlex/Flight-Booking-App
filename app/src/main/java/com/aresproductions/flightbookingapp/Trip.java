package com.aresproductions.flightbookingapp;

public class Trip {
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
}
