package com.example.kacyn.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final String MOVIE_FRAGMENT_TAG = "MFTAG";

    private String mSortPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSortPrefs = Utility.getSortPreferences(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MovieFragment(), MOVIE_FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings){
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String sortPrefs = Utility.getSortPreferences(this);
        // update the location in our second pane using the fragment manager
        if (sortPrefs != null && !sortPrefs.equals(mSortPrefs)) {

            if(!sortPrefs.equals("favorites")) {

                Log.v(LOG_TAG, "favorites not selected");

                MovieFragment mf = (MovieFragment) getSupportFragmentManager().findFragmentByTag(MOVIE_FRAGMENT_TAG);
                if (null != mf) {
                    mf.updateMovieData();
                }

            }
            else {
                Log.v(LOG_TAG, "favorites selected");
            }

            mSortPrefs = sortPrefs;
        }
    }

}
