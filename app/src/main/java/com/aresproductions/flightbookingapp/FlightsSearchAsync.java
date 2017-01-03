package com.aresproductions.flightbookingapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.aresproductions.flightbookingapp.BuildConfig.OPEN_WEATHER_MAP_API_KEY;

/**
 * Created by GIANNIS on 03-Jan-17.
 */

public class FlightsSearchAsync extends AsyncTask<String, Void, Flight[]> {

    private final String LOG_TAG = FlightsSearchAsync.class.getSimpleName();


    public AsyncResponse delegate = null;


    @Override
    protected void onPostExecute(Flight[] flights) {
        //delegate.process_Finish(flights);
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
                    .appendQueryParameter(apiKeyParam, OPEN_WEATHER_MAP_API_KEY)
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
            if (strings[8].equals("true")){
                builtUri = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter(apiKeyParam, OPEN_WEATHER_MAP_API_KEY)
                        .appendQueryParameter(queryParamOrigin, strings[0])
                        .appendQueryParameter(queryParamDestination, strings[1])
                        .appendQueryParameter(queryParamDeparture, strings[2])
                        .appendQueryParameter(queryParamAdults, strings[4])
                        .appendQueryParameter(queryParamChildren, strings[5])
                        .appendQueryParameter(queryParamInfants, strings[6])
                        .appendQueryParameter(queryParamClass, strings[7])
                        .build();
            }


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
            Log.v(LOG_TAG, "Flights JSON String: " + flightsJsonStr);
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
            return getFlightsDataFromJson(flightsJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the flights.
        return null;
    }

    //Processing Data from JSON File
    private Flight[] getFlightsDataFromJson(String flightsJsonStr) throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String FLIGHT_CURRENCY = "currency";
        final String FLIGHT_ORIGIN = "origin";
        final String FLIGHT_DESTINATION = "destination";
        final String FLIGHT_AIRLINE = "operating_airline";
        final String FLIGHT_CLASS = "travel_class";
        final String FLIGHT_PRICE = "total_price";

        JSONArray flightsJsonArray = new JSONArray(flightsJsonStr);
        Flight[] resultsFlights = new Flight[flightsJsonArray.length()];

        for (int i = 0; i < flightsJsonArray.length(); i++) {
            resultsFlights[i] = new Flight(flightsJsonArray.getJSONObject(i).getString(FLIGHT_CURRENCY),
                    flightsJsonArray.getJSONObject(i).getString(FLIGHT_ORIGIN),
                    flightsJsonArray.getJSONObject(i).getString(FLIGHT_DESTINATION),
                    flightsJsonArray.getJSONObject(i).getString(FLIGHT_AIRLINE),
                    flightsJsonArray.getJSONObject(i).getString(FLIGHT_CLASS),
                    flightsJsonArray.getJSONObject(i).getString(FLIGHT_PRICE));

        }

        return resultsFlights;

    }

}
