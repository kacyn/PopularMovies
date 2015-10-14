package com.example.kacyn.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private Movie mMovie;

    private int mMovieId;

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

        mTrailerAdapter = new TrailerAdapter(getActivity(), mNumTrailersFetched, mTrailerUrlArray);
        mTrailerListView = (ListView) rootView.findViewById(R.id.listview_trailer);
        mTrailerListView.setAdapter(mTrailerAdapter);

        mTrailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + mTrailerUrlArray.get(position)));
                startActivity(youtubeIntent);
            }
        });

        mReviewAdapter = new ReviewAdapter(getActivity(), mNumReviewsFetched, mReviewArray);
        mReviewListView = (ListView) rootView.findViewById(R.id.listview_review);
        mReviewListView.setAdapter(mReviewAdapter);

        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        updateReviewData();
        updateTrailerData();
    }

    private void updateReviewData(){
        FetchReviewTask fetchReviewTask = new FetchReviewTask();
        fetchReviewTask.execute();
    }

    private void updateTrailerData(){
        FetchTrailerTask fetchTrailerTask = new FetchTrailerTask();
        fetchTrailerTask.execute();
    }

    public class FetchReviewTask extends AsyncTask<Void, Void, Void> {

        private final String LOG_TAG = FetchReviewTask.class.getSimpleName();

        protected Void doInBackground(Void... params){

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String reviewJsonStr = null;

            try {
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath("" + mMovieId)
                        .appendPath("reviews")
                        .appendQueryParameter("api_key", getString(R.string.api_key));

                URL url = new URL(builder.build().toString());

                // Create the request and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                reviewJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                getReviewDataFromJson(reviewJsonStr);
            }
            catch(JSONException e)
            {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //update view
            mReviewAdapter.update(mNumReviewsFetched, mReviewArray);
        }

        private void getReviewDataFromJson(String reviewJsonStr) throws JSONException {
            mReviewArray.clear();

            final String RESULTS = "results";
            final String AUTHOR = "author";
            final String CONTENT = "content";
            final String URL = "url";

            String author;
            String content;
            String url;

            JSONObject reviewJson = new JSONObject(reviewJsonStr);
            JSONArray reviewArray = reviewJson.getJSONArray(RESULTS);

            mNumReviewsFetched = reviewArray.length();

            Log.v("Review Adapter", "num reviews fetched json: " + mNumReviewsFetched);

            for(int i = 0; i < mNumReviewsFetched; i++) {
                JSONObject reviewData = reviewArray.getJSONObject(i);

                author = reviewData.getString(AUTHOR);
                content = reviewData.getString(CONTENT);
                url = reviewData.getString(URL);

                mReviewArray.add(new Review(author, content, url));
            }
        }
    }

    public class FetchTrailerTask extends AsyncTask<Void, Void, Void> {

        private final String LOG_TAG = FetchTrailerTask.class.getSimpleName();

        protected Void doInBackground(Void... params){
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String trailerJsonStr = null;

            try {
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath("" + mMovieId)
                        .appendPath("videos")
                        .appendQueryParameter("api_key", getString(R.string.api_key));

                URL url = new URL(builder.build().toString());

                // Create the request and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                trailerJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                getTrailerDataFromJson(trailerJsonStr);
            }
            catch(JSONException e)
            {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //update view
            mTrailerAdapter.update(mNumTrailersFetched, mTrailerUrlArray);
        }

        private void getTrailerDataFromJson(String trailerJsonStr) throws JSONException {
            mTrailerUrlArray.clear();

            final String RESULTS = "results";
            final String KEY = "key";

            String key;

            JSONObject trailerJson = new JSONObject(trailerJsonStr);
            JSONArray trailerArray = trailerJson.getJSONArray(RESULTS);

            mNumTrailersFetched = trailerArray.length();

            Log.v("Trailer Adapter", "num trailers fetched json: " + mNumTrailersFetched);

            for(int i = 0; i < mNumTrailersFetched; i++) {
                JSONObject reviewData = trailerArray.getJSONObject(i);

                key = reviewData.getString(KEY);

                mTrailerUrlArray.add(key);
            }
        }
    }
}
