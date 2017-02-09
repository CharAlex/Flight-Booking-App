package com.aresproductions.flightbookingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class FlightsRecyclerActivity extends AppCompatActivity implements AsyncResponseFlight {

    private static final String TAG = "FlightsRecyclerActivity";

    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flights_recycler);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle data = getIntent().getExtras();
        SearchFlight searchFlight = data.getParcelable("searchFlight");

        FlightsSearchAsync asyncTask = new FlightsSearchAsync();
        asyncTask.delegate = FlightsRecyclerActivity.this;
        asyncTask.execute(searchFlight.getOrigin(),
                searchFlight.getDestination(),
                searchFlight.getDeparture(),
                searchFlight.getReturnDate(),
                searchFlight.getAdults(),
                searchFlight.getChildren(),
                searchFlight.getInfants(),
                searchFlight.getTravel_class(),
                searchFlight.getOneWay());

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void processFinish(Flight[] flights) {
        Log.d(TAG, "Finished Downloading the Flights and setting the adapter");
        FlightsAdapter newAdapter = new FlightsAdapter(flights);
        mRecyclerView.setAdapter(newAdapter);

    }

}
