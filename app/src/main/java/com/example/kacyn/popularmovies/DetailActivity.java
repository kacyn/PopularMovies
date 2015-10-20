package com.example.kacyn.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class DetailActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final String DETAIL_FRAGMENT_TAG = "DFTAG";

    private String mSortPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSortPrefs = Utility.getSortPreferences(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {

            DetailActivityFragment df = new DetailActivityFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, df)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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

   /* @Override
    protected void onResume() {
        super.onResume();

        String sortPrefs = Utility.getSortPreferences(this);
        // update the location in our second pane using the fragment manager
        if (sortPrefs != null && !sortPrefs.equals(mSortPrefs)) {
            DetailActivityFragment df = (DetailActivityFragment)getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG);
            if ( null != df ) {
                df.onSortPrefsChanged();
            }
            mSortPrefs = sortPrefs;
        }
    }*/
}
