package com.example.kacyn.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MovieFragment.Callback {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    //private final String MOVIE_FRAGMENT_TAG = "MFTAG";
    private final String DETAIL_FRAGMENT_TAG = "DFTAG";

    private boolean mTwoPane;
    private String mSortPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "in on create");
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "created super!");
        mSortPrefs = Utility.getSortPreferences(this);
        Log.v(LOG_TAG, "retrieved sort prefs");

        setContentView(R.layout.activity_main);
        Log.v(LOG_TAG, "created main activity");

        if (findViewById(R.id.detail_container) != null) {

            Log.v(LOG_TAG, "In tablet case");

            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.detail_container, new DetailActivityFragment(), DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        }
        else {
            Log.v(LOG_TAG, "in phone case");

            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        MovieFragment movieFragment = ((MovieFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_movie));
        //movieFragment.setUseTodayLayout(!mTwoPane);
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
            MovieFragment mf = (MovieFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_movie);
            if (null != mf) {
                if(sortPrefs.equals("favorites")) mf.updateFavoritesLoader();
                else mf.updateMovieData();
            }

            DetailActivityFragment df = (DetailActivityFragment)getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG);
            if( null != df ) {
                //df.updateMo
            }

            mSortPrefs = sortPrefs;
        }
    }

    @Override
    public void onItemSelected(int movieId) {
        Log.v(LOG_TAG, "in on item selected.  movie id: " + movieId);

        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putInt(getString(R.string.detail_args), movieId);

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, fragment, DETAIL_FRAGMENT_TAG)
                    .commit();
        }
        else {
            Log.v(LOG_TAG, "checking that i made it to the phone view.  movie id: " + movieId);

            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(getString(R.string.movie_intent), movieId);
            startActivity(intent);
        }
    }
}
