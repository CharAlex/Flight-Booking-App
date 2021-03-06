package com.aresproductions.flightbookingapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.aresproductions.flightbookingapp.BuildConfig.AmadeusApiKey;

/**
 * Created by GIANNIS on 03-Jan-17.
 */

public class FlightsSearchAsync extends AsyncTask<String, Void, Flight[]> {

    private final String LOG_TAG = FlightsSearchAsync.class.getSimpleName();


    public AsyncResponseFlight delegate = null;


    @Override
    protected void onPostExecute(Flight[] flights) {
        delegate.processFinish(flights);
    }


    @Override
    protected Flight[] doInBackground(String... strings) {
        Log.d("Json", "Process Started");


        //String still empty?
        if (strings.length == 0) {
            return null;
        }
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;


        // Will contain the raw JSON response as a string.
        String flightsJsonStr = null;
        String airportFormat = "json";

        try {
            // Construct the URL for the query

            final String baseUrl = "https://api.sandbox.amadeus.com/v1.2/flights/low-fare-search?";
            final String queryParamOrigin = "origin";
            final String queryParamDestination = "destination";
            final String queryParamDeparture = "departure_date";
            final String queryParamReturn = "return_date";
            final String queryParamAdults = "adults";
            final String queryParamChildren = "children";
            final String queryParamInfants = "infants";
            final String queryParamClass = "travel_class";
            final String queryParamOneWay = "one-way";

            final String apiKeyParam = "apikey";

            Uri builtUri = Uri.parse(baseUrl).buildUpon()
                    .appendQueryParameter(apiKeyParam, AmadeusApiKey)
                    .appendQueryParameter(queryParamOrigin, strings[0])
                    .appendQueryParameter(queryParamDestination, strings[1])
                    .appendQueryParameter(queryParamDeparture, strings[2])
                    .appendQueryParameter(queryParamReturn, strings[3])
                    .appendQueryParameter(queryParamAdults, strings[4])
                    .appendQueryParameter(queryParamChildren, strings[5])
                    .appendQueryParameter(queryParamInfants, strings[6])
                    .appendQueryParameter(queryParamClass, strings[7])
                    .build();

            //If we have One Way trip then this is our link
            if (strings[8].equals("true")) {
                builtUri = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter(apiKeyParam, AmadeusApiKey)
                        .appendQueryParameter(queryParamOrigin, strings[0])
                        .appendQueryParameter(queryParamDestination, strings[1])
                        .appendQueryParameter(queryParamDeparture, strings[2])
                        .appendQueryParameter(queryParamAdults, strings[4])
                        .appendQueryParameter(queryParamChildren, strings[5])
                        .appendQueryParameter(queryParamInfants, strings[6])
                        .appendQueryParameter(queryParamClass, strings[7])
                        .build();
            }

            Log.d("Roundtrip", strings[8]);


            URL url = new URL(builtUri.toString());
            Log.v(LOG_TAG, "Built URI: " + builtUri.toString());

            // Create the request, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            flightsJsonStr = buffer.toString();
            //Log.v(LOG_TAG, "Flights JSON String: " + flightsJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the flights data, there's no point in attempting
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        try {
            return getFlightsDataFromJson(flightsJsonStr, strings[8]);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the flights.
        return null;
    }

    //Processing Data from JSON File
    private Flight[] getFlightsDataFromJson(String flightsJsonStr, String oneway) throws JSONException {
        JSONObject flightsJson = new JSONObject(flightsJsonStr);
        JSONArray flightsJsonArray = flightsJson.getJSONArray("results");

        Flight[] searchedFlights = new Flight[flightsJsonArray.length()];


        for (int i = 0; i < flightsJsonArray.length(); i++) {

            // Get the JSON object representing the itineraries
            JSONObject startObject = flightsJsonArray.getJSONObject(i);
            JSONObject itineraries = startObject.getJSONArray("itineraries").getJSONObject(0);
            JSONObject outbound = itineraries.getJSONObject("outbound");
            JSONArray flightsArray = outbound.getJSONArray("flights");

            //JSONObject flight

            ArrayList<Trip> tempTripsOutbound = new ArrayList<>();

            for (int j = 0; j < flightsArray.length(); j++) {
                JSONObject airportOrigin = flightsArray.getJSONObject(j).getJSONObject("origin");
                JSONObject airportDestination = flightsArray.getJSONObject(j).getJSONObject("destination");
                tempTripsOutbound.add(new Trip(flightsArray.getJSONObject(j).getString("departs_at"),
                        flightsArray.getJSONObject(j).getString("arrives_at"),
                        airportOrigin.getString("airport"),
                        airportDestination.getString("airport")));


                //Log.d("Origin airport: ", airportOrigin.getString("airport"));
                //Log.d("Destination airport: ", airportDestination.getString("airport"));
                //Log.d("departs_at",flightsArray.getJSONObject(j).getString("departs_at"));
            }
            JSONObject fareArray = startObject.getJSONObject("fare");


            //Log.d(FLIGHT_DESTINATION,flightsArray.getJSONObject(0).getString(FLIGHT_DESTINATION));
            //Log.d("marketing_airline",flightsArray.getJSONObject(0).getString("marketing_airline"));
            //Log.d("total_price",fareArray.getString("total_price"));
            //Flight(String currency, String origin, String destination, String airline, String travelClass, String departs_at, String arrives_at, String price)

            ArrayList<Trip> tempTripsInbound = new ArrayList<>();
            if (oneway.equals("false")) {
                for (int j = 0; j < flightsArray.length(); j++) {
                    JSONObject airportOrigin = flightsArray.getJSONObject(j).getJSONObject("origin");
                    JSONObject airportDestination = flightsArray.getJSONObject(j).getJSONObject("destination");
                    tempTripsInbound.add(new Trip(flightsArray.getJSONObject(j).getString("departs_at"),
                            flightsArray.getJSONObject(j).getString("arrives_at"),
                            airportOrigin.getString("airport"),
                            airportDestination.getString("airport")));
                }
            }


            if (oneway.equals("false")) {
                searchedFlights[i] = new FlightRoundTrip("USD",
                        tempTripsOutbound.get(0).getDepart_airport(),
                        tempTripsOutbound.get(tempTripsOutbound.size() - 1).getArrival_airport(),
                        flightsArray.getJSONObject(0).getString("marketing_airline"),
                        "travel_class",
                        tempTripsOutbound,
                        fareArray.getString("total_price"),
                        tempTripsInbound);
            } else {
                searchedFlights[i] = new Flight("USD",
                        tempTripsOutbound.get(0).getDepart_airport(),
                        tempTripsOutbound.get(tempTripsOutbound.size() - 1).getArrival_airport(),
                        flightsArray.getJSONObject(0).getString("marketing_airline"),
                        "travel_class",
                        tempTripsOutbound,
                        fareArray.getString("total_price"));
            }


        }


        return searchedFlights;

    }

}
