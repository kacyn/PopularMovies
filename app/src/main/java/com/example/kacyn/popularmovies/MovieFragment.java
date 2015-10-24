package com.example.kacyn.popularmovies;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.example.kacyn.popularmovies.data.MovieContract;

public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public static String LOG_TAG = MovieFragment.class.getSimpleName();

    private MovieAdapter mMovieAdapter;
    private GridView mGridView;

    private int mPosition = GridView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";

    private static final int MOVIE_LOADER = 0;

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_POSTER_URL,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID
    };

    static final int COL_ID = 0;
    static final int COL_POSTER_URL = 1;
    static final int COL_MOVIE_ID = 2;

    public MovieFragment() {
        Log.v(LOG_TAG, "in constructor of movie fragment");
    }

    public interface Callback {
        void onItemSelected(int movieId);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        //first time view created
        if(savedInstanceState == null){
            updateMovieData();
        }

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mMovieAdapter = new MovieAdapter(getActivity(), null, 0);

        mGridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        View emptyView = rootView.findViewById(R.id.empty_gridview);
        mGridView.setEmptyView(emptyView);
        mGridView.setAdapter(mMovieAdapter);

        //trigger callback when movie selected
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    ((Callback) getActivity())
                            .onItemSelected(cursor.getInt(COL_MOVIE_ID));
                }

                mPosition = position;
            }
        });

        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    public void updateMovieData(){
        FetchMovieTask fetchMovieTask = new FetchMovieTask(getActivity());//, mMovieAdapter);
        fetchMovieTask.execute();
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }

    public void updateFavoritesLoader() {
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortPref = Utility.getSortPreferences(getActivity());

        switch (sortPref) {
            case "popularity":
                return new CursorLoader(
                        getActivity(),
                        MovieContract.MovieEntry.CONTENT_URI,
                        MOVIE_COLUMNS,
                        null,
                        null,
                        "" + MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC LIMIT 20"
                );
            case "vote_average":
                return new CursorLoader(
                        getActivity(),
                        MovieContract.MovieEntry.CONTENT_URI,
                        MOVIE_COLUMNS,
                        null,
                        null,
                        "" + MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " DESC LIMIT 20"
                );
            case "favorites":
                return new CursorLoader(
                        getActivity(),
                        MovieContract.MovieEntry.CONTENT_URI,
                        MOVIE_COLUMNS,
                        MovieContract.MovieEntry.COLUMN_MARKED_FAVORITE + " = ?",
                        new String[]{"1"},
                        "" + MovieContract.MovieEntry.COLUMN_MARKED_FAVORITE + " DESC"
                );
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mMovieAdapter.swapCursor(cursor);
        if (mPosition != ListView.INVALID_POSITION) {
            mGridView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }

}
