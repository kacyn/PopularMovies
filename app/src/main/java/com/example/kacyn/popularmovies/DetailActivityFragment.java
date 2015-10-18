package com.example.kacyn.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import android.widget.ListView;

import com.example.kacyn.popularmovies.data.MovieContract;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    private static final int DETAIL_LOADER = 0;
    private static final int REVIEW_LOADER = 1;
    private static final int TRAILER_LOADER = 2;
    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.

    private static final String[] DETAIL_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_POSTER_URL,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_SYNOPSIS
    };

    private static final String[] REVIEW_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            MovieContract.ReviewEntry.TABLE_NAME + "." + MovieContract.ReviewEntry._ID,
            MovieContract.ReviewEntry.COLUMN_AUTHOR,
            MovieContract.ReviewEntry.COLUMN_CONTENT
    };

    private static final String[] TRAILER_COLUMNS = {
            MovieContract.TrailerEntry.TABLE_NAME + "." + MovieContract.TrailerEntry._ID,
            MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_DETAIL_ID = 0;
    static final int COL_DETAIL_POSTER_URL = 1;
    static final int COL_DETAIL_TITLE = 2;
    static final int COL_DETAIL_VOTE_AVERAGE = 3;
    static final int COL_DETAIL_RELEASE_DATE = 4;
    static final int COL_DETAIL_SYNOPSIS = 5;

    static final int COL_REVIEW_ID = 0;
    static final int COL_REVIEW_AUTHOR = 1;
    static final int COL_REVIEW_CONTENT = 2;

    static final int COL_TRAILER_ID = 0;
    static final int COL_TRAILER_KEY = 1;

    private Movie mMovie;

    private int mMovieId;

    private DetailAdapter mDetailAdapter;
    private ListView mDetailListView;

    private ReviewAdapter mReviewAdapter;
    private ListView mReviewListView;
    private int mNumReviewsFetched;
    private ArrayList<Review> mReviewArray = new ArrayList<Review>();

    private TrailerAdapter mTrailerAdapter;
    private ListView mTrailerListView;
    private int mNumTrailersFetched;
    private ArrayList<String> mTrailerUrlArray = new ArrayList<String>();

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        mMovie = intent.getParcelableExtra("MovieIntent");

        mMovieId = mMovie.mMovieId;

        mDetailAdapter = new DetailAdapter(getActivity(), null, 0);
        mDetailListView = (ListView) rootView.findViewById(R.id.listview_detail);
        mDetailListView.setAdapter(mDetailAdapter);


       // mTrailerAdapter = new TrailerAdapter(getActivity(), mNumTrailersFetched, mTrailerUrlArray);
        mTrailerAdapter = new TrailerAdapter(getActivity(), null, 0);
        mTrailerListView = (ListView) rootView.findViewById(R.id.listview_trailer);
        mTrailerListView.setAdapter(mTrailerAdapter);

        //mAuthorView = (TextView) rootView.findViewById(R.id.list_item_review_author);
        //mContentView = (TextView) rootView.findViewById(R.id.list_item_review_content);

        mTrailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {

                    Log.v(LOG_TAG, "poster url: " + cursor.getString(COL_DETAIL_POSTER_URL));

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + cursor.getString(COL_DETAIL_POSTER_URL)));
                    startActivity(intent);
                }

                //Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + mTrailerUrlArray.get(position)));
                //startActivity(youtubeIntent);
            }
        });

        mReviewAdapter = new ReviewAdapter(getActivity(), null, 0);

        //mReviewAdapter = new ReviewAdapter(getActivity(), mNumReviewsFetched, mReviewArray);
        mReviewListView = (ListView) rootView.findViewById(R.id.listview_review);
        mReviewListView.setAdapter(mReviewAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(REVIEW_LOADER, null, this);
        getLoaderManager().initLoader(TRAILER_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart(){
        super.onStart();

        updateReviewData();
        //getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        updateTrailerData();
    }

    private void updateReviewData(){
        Log.v(LOG_TAG, "In update review data");

        FetchReviewTask fetchReviewTask = new FetchReviewTask(getActivity());
        fetchReviewTask.execute(mMovieId);
    }

    private void updateTrailerData(){
        FetchTrailerTask fetchTrailerTask = new FetchTrailerTask(getActivity());
        fetchTrailerTask.execute(mMovieId);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {

        Log.v(LOG_TAG, "in on create loader");

        Uri detailUri = MovieContract.MovieEntry.buildDetailWithMovie(mMovieId);

        Log.v(LOG_TAG, "detail uri: " + detailUri);

        Uri reviewUri = MovieContract.ReviewEntry.buildReviewWithMovie(mMovieId);
        Uri trailerUri = MovieContract.TrailerEntry.buildTrailerWithMovie(mMovieId);

        switch(loaderId) {
            case DETAIL_LOADER:
                Log.v(LOG_TAG, "returning detail loader");
                return new CursorLoader(
                        getActivity(),
                        detailUri,
                        DETAIL_COLUMNS,
                        null,
                        null,
                        null
                );
            case REVIEW_LOADER:
                return new CursorLoader(
                        getActivity(),
                        reviewUri,
                        REVIEW_COLUMNS,
                        null,
                        null,
                        null);
            case TRAILER_LOADER:
                return new CursorLoader(
                        getActivity(),
                        trailerUri,
                        TRAILER_COLUMNS,
                        null,
                        null,
                        null);
            default:
                return null;
        }
        //Log.v("Review Adapter", "review uri: " + reviewUri);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.v(LOG_TAG, "in on load finished");

        switch (loader.getId()) {
            case DETAIL_LOADER:
                mDetailAdapter.swapCursor(cursor);
                break;
            case REVIEW_LOADER:
                mReviewAdapter.swapCursor(cursor);
                break;
            case TRAILER_LOADER:
                mTrailerAdapter.swapCursor(cursor);
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "in onLoaderReset");

        switch (loader.getId()) {
            case DETAIL_LOADER:
                mDetailAdapter.swapCursor(null);
                break;
            case REVIEW_LOADER:
                mReviewAdapter.swapCursor(null);
                break;
            case TRAILER_LOADER:
                mTrailerAdapter.swapCursor(null);
                break;
            default:
                break;
        }
    }
}
