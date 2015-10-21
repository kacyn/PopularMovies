package com.example.kacyn.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
/*
public class SpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {

    public static String LOG_TAG = SpinnerActivity.class.getSimpleName();

    private int mPos;
    private String mSelection;

    public static final int DEFAULT_POS = 0;
    public static final String PREFERENCES_FILE = "SpinnerPrefs";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Spinner spinner = (Spinner) findViewById(R.id.sort_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.pref_sort_options, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        //attach spinner activity
        SpinnerActivity spinnerActivity = new SpinnerActivity();
        spinner.setOnItemSelectedListener(spinnerActivity);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {

        mPos = pos;
        mSelection = parent.getItemAtPosition(pos).toString();

        // An item was selected. You can retrieve the selected item using
        Log.v(LOG_TAG, "item selected: " + parent.getItemAtPosition(pos));


    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!readInstanceState(this)) {
            setInitialState();
        }

        Spinner restoreSpinner = (Spinner) findViewById(R.id.sort_spinner);
        restoreSpinner.setSelection(mPos);
    }

    @Override
    public void onPause() {
        super.onPause();

        if(!writeInstanceState(this)) {
            Log.v(LOG_TAG, "Failed to write state");
        }
    }

    public void setInitialState() {
        mPos = DEFAULT_POS;
    }

    public boolean readInstanceState(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_FILE, MODE_PRIVATE);

        mPos = prefs.getInt(getString(R.string.pref_sort_key), SpinnerActivity.DEFAULT_POS);
        mSelection = prefs.getString(getString(R.string.pref_sort_key), "");

        return prefs.contains(getString(R.string.pref_sort_key));
    }

    public boolean writeInstanceState(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_FILE, MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(getString(R.string.pref_sort_key), mSelection);
        return editor.commit();
    }


}


*/