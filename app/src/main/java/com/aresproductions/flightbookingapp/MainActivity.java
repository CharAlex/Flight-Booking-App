package com.aresproductions.flightbookingapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements AsyncResponse {

    private TextView departureDateText;
    private TextView arrivalDateText;

    private LinearLayout departure;
    private LinearLayout arrival;

    private int day_x, month_x, year_x;
    private boolean isDeparture;
    private static final int DIALOG_ID = 0;

    private AutoCompleteTextView departureAutoComplete;
    private AutoCompleteTextView destinationAutoComplete;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        showCalendars();
        //Get the entries for spinners
        setSpinnerData(R.id.spinner1,R.array.travel_class);
        setSpinnerData(R.id.spinner_adult,R.array.num_of_passengers);
        setSpinnerData(R.id.spinner_children,R.array.num_of_passengers);
        setSpinnerData(R.id.spinner_infant,R.array.num_of_passengers);

        setupAutocompleteTextViews();

        //Swap button (destination,departure)
        Button swap_button = (Button) findViewById(R.id.swap_button);
        swap_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                departureAutoComplete = (AutoCompleteTextView) findViewById(R.id.search_departure);
                destinationAutoComplete = (AutoCompleteTextView) findViewById(R.id.search_destination);
                String search_temp = destinationAutoComplete.getText().toString();
                destinationAutoComplete.setText(departureAutoComplete.getText());
                departureAutoComplete.setText(search_temp);


            }
        });



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
            String date = day_x + "/" + month_x + "/" + year_x;
            if(isDeparture){
                departureDateText.setText(date);
            }else{
                arrivalDateText.setText(date);
            }
            Toast.makeText(MainActivity.this,date,Toast.LENGTH_SHORT).show();
        }

    };

    /*public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        //TextView dateText;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            String date = day + "/" + (month + 1) + "/" + year;
            //dateText.setText(date);
            Toast.makeText(getContext(), date, Toast.LENGTH_LONG).show();


        }
    }


    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
        //dateText.setText("Bla bla");

    }*/

    public void showCalendars(){
        departureDateText = (TextView) findViewById(R.id.departure_text);
        arrivalDateText = (TextView) findViewById(R.id.arrival_text);

        final Calendar c = Calendar.getInstance();
        year_x = c.get(Calendar.YEAR);
        month_x = c.get(Calendar.MONTH);
        day_x = c.get(Calendar.DAY_OF_MONTH);

        //Show the current date
        String date = day_x + "/" + (month_x+1) + "/" + year_x;
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
    public void processFinish(String[] output) {
        ArrayAdapter<String> adapterSearch;
        adapterSearch = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, output);

        departureAutoComplete.setAdapter(adapterSearch);
        destinationAutoComplete.setAdapter(adapterSearch);
        adapterSearch.notifyDataSetChanged();
    }
}

