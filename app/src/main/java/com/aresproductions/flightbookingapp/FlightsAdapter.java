/*
* Copyright (C) 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.aresproductions.flightbookingapp;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class FlightsAdapter extends RecyclerView.Adapter<FlightsAdapter.ViewHolder> {
    private static final String TAG = "FlightsAdapter";

    private Flight[] flightsData;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView total_price, departure_city, arrival_city, trip_time, trip_length, stops;


        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getPosition() + " clicked.");
                }
            });
            total_price = (TextView) v.findViewById(R.id.total_price);
            departure_city = (TextView) v.findViewById(R.id.departure_city);
            arrival_city = (TextView) v.findViewById(R.id.arrival_city);
            trip_time = (TextView) v.findViewById(R.id.trip_time);
            trip_length = (TextView) v.findViewById(R.id.trip_length);
            stops = (TextView) v.findViewById(R.id.stops);


        }

        public TextView getTotal_price() {
            return total_price;
        }

        public TextView getDeparture_city() {
            return departure_city;
        }

        public TextView getArrival_city() {
            return arrival_city;
        }

        public TextView getTrip_time() {
            return trip_time;
        }

        public TextView getTrip_length() {
            return trip_length;
        }

        public TextView getStops() {
            return stops;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param flightsData Flight[] containing the data to populate views to be used by RecyclerView.
     */
    public FlightsAdapter(Flight[] flightsData) {
        //Log.d("Length", flightsData.length + "");
        this.flightsData = flightsData;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.flight_row_item, viewGroup, false);

        return new ViewHolder(v);
    }

    // Add content for each item here
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        //Log.d(TAG, "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.getTotal_price().setText(flightsData[position].getPrice());
        viewHolder.getDeparture_city().setText(flightsData[position].getOrigin());
        viewHolder.getArrival_city().setText(flightsData[position].getDestination());
        viewHolder.getTrip_time().setText(flightsData[position].getTrips().get(0).getDeparts_at().substring(11)
                + " - " + flightsData[position].getTrips().get(flightsData[position].getTrips().size() - 1).getArrives_at().substring(11));

        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH);
            Date startDate = format.parse(flightsData[position].getTrips().get(0).getDeparts_at());
            Date endDate = format.parse(flightsData[position].getTrips().get(flightsData[position].getTrips().size() - 1).getArrives_at());// Set end date
            long duration = endDate.getTime() - startDate.getTime();
            long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);
            long diffInMin = TimeUnit.MILLISECONDS.toMinutes(duration) % 60;
            viewHolder.getTrip_length().setText((diffInHours) + "h " + diffInMin + "m");

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(flightsData[position].getTrips().size() > 1)
            viewHolder.getStops().setText(flightsData[position].getTrips().size() + " stops");

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return flightsData.length;
    }

}
