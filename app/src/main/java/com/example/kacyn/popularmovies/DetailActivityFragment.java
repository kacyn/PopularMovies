package com.example.kacyn.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private Movie mMovie;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        mMovie = intent.getParcelableExtra("MovieIntent");

        ImageView posterImageView = (ImageView) rootView.findViewById(R.id.poster_image);
        Picasso.with(getActivity()).load(mMovie.mPosterUrl).into(posterImageView);

        // Create the text views
        TextView titleTextView = (TextView) rootView.findViewById(R.id.title_text);
        titleTextView.setText(mMovie.mTitle);

        TextView voteAvgTextView = (TextView) rootView.findViewById(R.id.vote_avg_text);
        voteAvgTextView.setText("Vote Average: " + mMovie.mVoteAvg);

        TextView releaseDateTextView = (TextView) rootView.findViewById(R.id.release_date_text);
        releaseDateTextView.setText("Release Date: " + mMovie.mReleaseDate);

        TextView synopsisTextView = (TextView) rootView.findViewById(R.id.synopsis_text);
        synopsisTextView.setText(mMovie.mSynopsis);

        return rootView;
    }
}
