package com.example.kacyn.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kacyn.popularmovies.data.MovieContract;
import com.example.kacyn.popularmovies.data.MovieDbHelper;
import com.squareup.picasso.Picasso;

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
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_POSTER_URL,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_SYNOPSIS
    };

    private static final String[] REVIEW_COLUMNS = {
            MovieContract.ReviewEntry._ID,
            MovieContract.ReviewEntry.COLUMN_AUTHOR,
            MovieContract.ReviewEntry.COLUMN_CONTENT
    };

    private static final String[] TRAILER_COLUMNS = {
            MovieContract.TrailerEntry._ID,
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

    private int mMovieId;

    public ImageView mPosterView;
    public TextView mTitleView;
    public TextView mVoteAvgView;
    public TextView mReleaseDateView;
    public TextView mSynopsisView;

    public LinearLayout mReviewLayout;
    public LinearLayout mTrailerLayout;


    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();

        mMovieId = intent.getIntExtra("MovieIntent", -1);

        mPosterView = (ImageView) rootView.findViewById(R.id.poster_image);
        mTitleView = (TextView) rootView.findViewById(R.id.title_text);
        mVoteAvgView = (TextView) rootView.findViewById(R.id.vote_avg_text);
        mReleaseDateView = (TextView) rootView.findViewById(R.id.release_date_text);
        mSynopsisView = (TextView) rootView.findViewById(R.id.synopsis_text);


        mReviewLayout = (LinearLayout) rootView.findViewById(R.id.review_layout);
        mTrailerLayout = (LinearLayout) rootView.findViewById(R.id.trailer_layout);

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
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {

        Uri detailUri = MovieContract.MovieEntry.buildDetailWithMovie(mMovieId);
        Uri reviewUri = MovieContract.ReviewEntry.buildReviewWithMovie(mMovieId);
        Uri trailerUri = MovieContract.TrailerEntry.buildTrailerWithMovie(mMovieId);

        switch(loaderId) {
            case DETAIL_LOADER:
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
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        switch (loader.getId()) {
            case DETAIL_LOADER:
                if(cursor != null && cursor.moveToFirst()) {

                    Picasso.with(getActivity()).load(cursor.getString(DetailActivityFragment.COL_DETAIL_POSTER_URL)).into(mPosterView);

                    String title = cursor.getString(DetailActivityFragment.COL_DETAIL_TITLE);
                    mTitleView.setText(title);

                    double voteAvg = cursor.getDouble(DetailActivityFragment.COL_DETAIL_VOTE_AVERAGE);
                    mVoteAvgView.setText("Vote Average: " + voteAvg);

                    String releaseDate = cursor.getString(DetailActivityFragment.COL_DETAIL_RELEASE_DATE);
                    mReleaseDateView.setText("Release Date: " + releaseDate);

                    String synopsis = cursor.getString(DetailActivityFragment.COL_DETAIL_SYNOPSIS);
                    mSynopsisView.setText(synopsis);
                }

                break;
            case REVIEW_LOADER:

                for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                    TextView authorView = new TextView(getActivity());
                    String author = cursor.getString(DetailActivityFragment.COL_REVIEW_AUTHOR);
                    authorView.setText("Author: " + author);

                    mReviewLayout.addView(authorView);


                    TextView contentView = new TextView(getActivity());
                    String content = cursor.getString(DetailActivityFragment.COL_REVIEW_CONTENT);
                    contentView.setText("Content: " + content);

                    mReviewLayout.addView(contentView);

                }

                break;
            case TRAILER_LOADER:

                for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                    Button button = new Button(getActivity());

                    button.setId(cursor.getPosition());
                    button.setText("Play Trailer " + (cursor.getPosition() + 1));

                    final String trailerUrl = cursor.getString(COL_DETAIL_POSTER_URL);

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailerUrl));
                            startActivity(intent);
                        }
                    });

                    mTrailerLayout.addView(button);

                }

                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
