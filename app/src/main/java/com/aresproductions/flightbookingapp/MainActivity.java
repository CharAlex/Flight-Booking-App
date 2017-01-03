package com.aresproductions.flightbookingapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements AsyncResponse {
    private static final String TAG = "MainActivity";


    private TextView departureDateText;
    private TextView arrivalDateText;

    private LinearLayout departure;
    private LinearLayout arrival;

    private int day_x, month_x, year_x;
    private boolean isDeparture;
    private static final int DIALOG_ID = 0;

    private AutoCompleteTextView departureAutoComplete;
    private AutoCompleteTextView destinationAutoComplete;

    private Button searchButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(!isConnectedToInternet(getBaseContext())){
            showCloseDialog();
        }

        showCalendars();
        //Get the entries for spinners
        setSpinnerData(R.id.spinner1,R.array.travel_class);
        setSpinnerData(R.id.spinner_adult,R.array.num_of_adults);
        setSpinnerData(R.id.spinner_children,R.array.num_of_passengers);
        setSpinnerData(R.id.spinner_infant,R.array.num_of_passengers);

        //Autocomplete Functionality
        setupAutocompleteTextViews();

        //Swap button (destination,departure)
        swapButton();
        //One way or Round Trip
        oneWayOrRoundTrip();

        //Search Button
        setSearchButton();

    }

    private void showCloseDialog() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_close_app)
                .setTitle(R.string.no_internet_title)
                .setMessage(R.string.no_internet_message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .show();
    }

    boolean isConnectedToInternet(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    private void setSearchButton() {
        searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Async Task for Flights
                getInfoForFlightSearchAsync();

                //Goes to Second Activity
                Intent intent = new Intent(getApplicationContext(), FlightsRecyclerActivity.class);
                startActivity(intent);
            }
        });
    }

    public void getInfoForFlightSearchAsync(){

        //Get values from AutoComplete texts
        String origin = departureAutoComplete.getText().toString().substring(departureAutoComplete.getText().length()-4,departureAutoComplete.getText().length()-1);
        String destination = destinationAutoComplete.getText().toString().substring(destinationAutoComplete.getText().length()-4,destinationAutoComplete.getText().length()-1);

        //Get values from calendars
        String departure = departureDateText.getText().toString();
        departure = departure.substring(6,10)+ "-" + departure.substring(3,5)+ "-" + departure.substring(0,2) ;

        String returndate = arrivalDateText.getText().toString();
        returndate =  returndate.substring(6,10)+ "-" + returndate.substring(3,5)+ "-" + returndate.substring(0,2) ;

        //Get values from spinners
        Spinner spinnerAdults = (Spinner) findViewById(R.id.spinner_adult);
        String adults = spinnerAdults.getSelectedItem().toString();

        Spinner spinnerChildren = (Spinner) findViewById(R.id.spinner_children);
        String children = spinnerChildren.getSelectedItem().toString();

        Spinner spinnerInfants = (Spinner) findViewById(R.id.spinner_infant);
        String infants = spinnerInfants.getSelectedItem().toString();

        Spinner spinnerClass = (Spinner) findViewById(R.id.spinner1);
        String travel_class = spinnerClass.getSelectedItem().toString();

        if(travel_class.equals("Economy")){
            travel_class = "ECONOMY";
        }else if (travel_class.equals("Premium Economy")){
            travel_class = "PREMIUM_ECONOMY";
        }else if (travel_class.equals("Business Class")){
            travel_class = "BUSINESS";
        }else if (travel_class.equals("First Class")){
            travel_class = "FIRST";
        }

        //One way or Round trip ??? If it is One Way then......
        RadioButton oneWayButton = (RadioButton) findViewById(R.id.one_way_button);
        String oneWay = "false";
        if(oneWayButton.isChecked()){
            oneWay = "true";
        }

        //Async Task
        FlightsSearchAsync asyncTask = new FlightsSearchAsync();
        asyncTask.delegate = MainActivity.this;
        asyncTask.execute(origin,
                destination,departure,returndate,adults,children,infants,travel_class,oneWay);


    }


    private void setupAutocompleteTextViews(){
        departureAutoComplete = (AutoCompleteTextView) findViewById(R.id.search_departure);
        departureAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(!departureAutoComplete.getText().toString().equals("")){
                    AirportAutocompleteAsync asyncTask = new AirportAutocompleteAsync();
                    asyncTask.delegate = MainActivity.this;
                    asyncTask.execute(departureAutoComplete.getText().toString());
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        destinationAutoComplete = (AutoCompleteTextView) findViewById(R.id.search_destination);
        destinationAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(!destinationAutoComplete.getText().toString().equals("")){
                    AirportAutocompleteAsync asyncTask = new AirportAutocompleteAsync();
                    asyncTask.delegate = MainActivity.this;
                    asyncTask.execute(destinationAutoComplete.getText().toString());
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void showDialogOnLayoutClick(){
        departure = (LinearLayout) findViewById(R.id.departure_date_view);
        arrival = (LinearLayout) findViewById(R.id.arrive_date_view);
        departure.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        isDeparture = true;
                        showDialog(DIALOG_ID);
                    }
                }
        );

        arrival.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        isDeparture = false;
                        showDialog(DIALOG_ID);
                    }
                }
        );
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        DatePickerDialog picker = new DatePickerDialog(this,datePickerListener,year_x, month_x, day_x);
        picker.getDatePicker().setMinDate(System.currentTimeMillis()-1000);

        if (id==DIALOG_ID)
            return picker;
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener(){

        public void onDateSet(DatePicker view, int year, int month, int day) {
            year_x = year;
            month_x = month+1;
            day_x = day;
            String date;

            if(month_x<10 && day_x<10){
                date = "0"+day_x + "/" + "0"+month_x + "/" + year_x;
            }else if(month_x<10){
                date = day_x + "/" + "0"+month_x + "/" + year_x;
            }else if(day_x<10){
                date = "0"+day_x + "/" + month_x + "/" + year_x;
            }else{
                date = day_x + "/" + month_x + "/" + year_x;
            }

            if(isDeparture){
                departureDateText.setText(date);
            }else{
                arrivalDateText.setText(date);
            }
            Toast.makeText(MainActivity.this,date,Toast.LENGTH_SHORT).show();
        }

    };

    public void showCalendars(){
        departureDateText = (TextView) findViewById(R.id.departure_text);
        arrivalDateText = (TextView) findViewById(R.id.arrival_text);

        final Calendar c = Calendar.getInstance();
        year_x = c.get(Calendar.YEAR);
        month_x = c.get(Calendar.MONTH);
        day_x = c.get(Calendar.DAY_OF_MONTH);

        //Show the current date
        String date;
        if(month_x<9 && day_x<10){
            date = "0"+day_x + "/" + "0"+ (month_x+1) + "/" + year_x;
        }else if(month_x<9){
            date = day_x + "/" + "0"+ (month_x+1) + "/" + year_x;
        }else if(day_x<10){
            date = "0"+day_x + "/" +  (month_x+1) + "/" + year_x;
        }else{
            date = day_x + "/" +  (month_x+1) + "/" + year_x;
        }
        departureDateText.setText(date);
        arrivalDateText.setText(date);

        showDialogOnLayoutClick();
    }

    public void setSpinnerData(int spinner_id, int array_name){
        Spinner spinner = (Spinner) findViewById(spinner_id);
        // Create an ArrayAdapter using the string array and a default spinner layout

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                array_name, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    public void swapButton(){
        final Button swap_button = (Button) findViewById(R.id.swap_button);
        swap_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                departureAutoComplete = (AutoCompleteTextView) findViewById(R.id.search_departure);
                destinationAutoComplete = (AutoCompleteTextView) findViewById(R.id.search_destination);
                String search_temp = destinationAutoComplete.getText().toString();
                destinationAutoComplete.setText(departureAutoComplete.getText());
                departureAutoComplete.setText(search_temp);
                swap_button.setBackgroundResource(R.drawable.swap_button_animation);
            }
        });
    }

    public void oneWayOrRoundTrip(){
        final RadioButton oneWayButton = (RadioButton) findViewById(R.id.one_way_button);
        final RadioButton roundTripButton = (RadioButton) findViewById(R.id.round_trip_button);
        final View divider = findViewById(R.id.calendar_divider);
        oneWayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oneWayButton.setChecked(true);
                roundTripButton.setChecked(false);
                arrival.setVisibility(View.INVISIBLE);
                divider.setVisibility(View.INVISIBLE);

                CardView temp_cardview = (CardView) findViewById(R.id.calendar_cardview);
                departure.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,temp_cardview.getHeight()));
                departure.setPadding(0,0,0,18);
                departure.setGravity(Gravity.CENTER);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,temp_cardview.getHeight());
                param.setMargins(0,16,0,0);
                temp_cardview.setLayoutParams(param);
            }
        });


        roundTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oneWayButton.setChecked(false);
                roundTripButton.setChecked(true);
                arrival.setVisibility(View.VISIBLE);
                departure.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,arrival.getHeight(),1));
                departure.setPadding(0,0,0,0);
                divider.setVisibility(View.VISIBLE);
                arrival.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,arrival.getHeight(),1));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void processFinish(Airport[] airports) {
        ArrayAdapter<String> adapterSearch;

        String[] tempStrings = new String[airports.length];
        for(int i = 0; i< airports.length;i++){
            tempStrings[i] = airports[i].getLabel();
        }

        adapterSearch = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, tempStrings);

        departureAutoComplete.setAdapter(adapterSearch);
        destinationAutoComplete.setAdapter(adapterSearch);
        adapterSearch.notifyDataSetChanged();
    }
}

