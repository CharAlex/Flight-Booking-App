package com.aresproductions.flightbookingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;

public class FlightsRecyclerActivity extends AppCompatActivity {

    private static final String TAG = "FlightsRecyclerActivity";

    protected RecyclerView mRecyclerView;
    protected FlightsAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    private String[] data = new String[50];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flights_recycler);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mAdapter = new FlightsAdapter(data);

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getApplicationContext());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        initializeData();
        mAdapter.notifyDataSetChanged();

    }

    private void initializeData() {
        //Here we should add an async task
        for(int i= 0; i<data.length; i++){
            data[i]= "Flight Number: "+i;
        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        super.onSaveInstanceState(savedInstanceState);
    }


    public void process_Finish(Flight[] flights) {
        ArrayAdapter<String> adapterSearch;

        String[] tempStrings = new String[flights.length];
        for(int i = 0; i< flights.length;i++){
            tempStrings[i] = flights[i].getPrice();
        }

        //mAdapter = new FlightsAdapter(tempStrings);

    }

}
