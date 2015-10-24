package com.example.kacyn.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.kacyn.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

public class FetchMovieTask extends AsyncTask<Void, Void, Void> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    private final Context mContext;

    public FetchMovieTask(Context context) {
        mContext = context;
    }

    protected Void doInBackground(Void... params){
        int numMoviesFetched = 20;
        String criterion = Utility.getSortPreferences(mContext);
        String order = "desc";
        int minVotes = 50;

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;

        try {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("discover")
                    .appendPath("movie")
                    .appendQueryParameter("sort_by", criterion + "." + order)
                    .appendQueryParameter("vote_count.gte", "" + minVotes)
                    .appendQueryParameter("api_key", mContext.getString(R.string.api_key));

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
            movieJsonStr = buffer.toString();
            return getMovieDataFromJson(movieJsonStr, numMoviesFetched);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } catch(JSONException e)
        {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }finally {
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

        return null;
    }

    private Void getMovieDataFromJson(String movieJsonStr, int numMovies) throws JSONException {
        final String RESULTS = "results";
        final String MOVIE_ID = "id";
        final String TITLE = "original_title";
        final String RELEASE_DATE = "release_date";
        final String VOTE_AVG = "vote_average";
        final String POPULARITY = "popularity";
        final String SYNOPSIS = "overview";
        final String POSTER_URL = "poster_path";

        int movieId;
        String title;
        String releaseDate;
        double voteAvg;
        double popularity;
        String synopsis;
        String posterUrl;

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(RESULTS);

        // Insert the new review information into the database
        Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());

        for(int i = 0; i < numMovies; i++) {
            // Get the JSON object representing the day
            JSONObject movieData = movieArray.getJSONObject(i);

            movieId = movieData.getInt(MOVIE_ID);
            title = movieData.getString(TITLE);
            releaseDate = movieData.getString(RELEASE_DATE);
            voteAvg = movieData.getDouble(VOTE_AVG);
            popularity = movieData.getDouble(POPULARITY);
            synopsis = movieData.getString(SYNOPSIS);
            posterUrl = "http://image.tmdb.org/t/p/w342" + movieData.getString(POSTER_URL);

            ContentValues movieValues = new ContentValues();

            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
            movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, voteAvg);
            movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, popularity);
            movieValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, synopsis);
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_URL, posterUrl);

            //update reviews and trailers based on movie id
            updateReviewData(movieId);
            updateTrailerData(movieId);

            cVVector.add(movieValues);

        }

        // add to database
        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
        }

        Log.v(LOG_TAG, "Fetch movie task Complete. " + cVVector.size() + " Inserted");

        return null;
    }

    private void updateReviewData(int movieId){
        FetchReviewTask fetchReviewTask = new FetchReviewTask(mContext);
        fetchReviewTask.execute(movieId);
    }

    private void updateTrailerData(int movieId){
        FetchTrailerTask fetchTrailerTask = new FetchTrailerTask(mContext);
        fetchTrailerTask.execute(movieId);
    }
}