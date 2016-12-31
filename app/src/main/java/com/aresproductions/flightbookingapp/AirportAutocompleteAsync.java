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
 * Created by Ares on 28-Dec-16.
 */

public class AirportAutocompleteAsync extends AsyncTask<String, Void, String[]> {

    private final String LOG_TAG = AirportAutocompleteAsync.class.getSimpleName();


    public AsyncResponse delegate = null;


    @Override
    protected void onPostExecute(String[] strings) {
        delegate.processFinish(strings);

    }




    @Override
    protected String[] doInBackground(String... strings) {
        Log.d("Json", "Process Started");


        //String still empty?
        if (strings.length == 0) {
            return null;
        }
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;


        // Will contain the raw JSON response as a string.
        String airportsJsonStr = null;
        String airportFormat = "json";

        try {
            // Construct the URL for the AirportAutocomplete query
            //https://api.sandbox.amadeus.com/v1.2/airports/autocomplete?apikey=<YOUR_KEY>&term=<Term Search>

            final String baseUrl = "http://api.sandbox.amadeus.com/v1.2/airports/autocomplete";
            final String queryParamTerm = "term";
            final String apiKeyParam = "apikey";

            Uri builtUri = Uri.parse(baseUrl).buildUpon()
                    .appendQueryParameter(apiKeyParam, OPEN_WEATHER_MAP_API_KEY)
                    .appendQueryParameter(queryParamTerm, strings[0])
                    .build();

            URL url = new URL(builtUri.toString());
            Log.v(LOG_TAG, "Built URI: " + builtUri.toString());

            // Create the request to Airport Autocomplete, and open the connection


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
            airportsJsonStr = buffer.toString();
            Log.v(LOG_TAG, "Airport JSON String: " + airportsJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the airport data, there's no point in attempting
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
            return getAirportDataFromJson(airportsJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the airports.
        return null;
    }

    //Processing Data from JSON File
    private String[] getAirportDataFromJson(String airportsJsonStr) throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String AIRPORT_VALUE = "value";
        final String AIRPORT_LABEL = "label";


        JSONArray airportsJsonArray = new JSONArray(airportsJsonStr);
        String[] resutlsStr = new String[airportsJsonArray.length()];

        for (int i = 0; i < airportsJsonArray.length(); i++) {
            resutlsStr[i] = airportsJsonArray.getJSONObject(i).getString(AIRPORT_LABEL);

        }

        return resutlsStr;

    }

}

