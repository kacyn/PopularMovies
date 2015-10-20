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

/**
 * Created by Kacyn on 10/16/2015.
 */
public class FetchReviewTask extends AsyncTask<Integer, Void, Void> {

    private final String LOG_TAG = FetchReviewTask.class.getSimpleName();

    private final Context mContext;

    public FetchReviewTask(Context context) {
        Log.v(LOG_TAG, "in constructor ");

        mContext = context;
    }

    private void getReviewDataFromJson(String reviewJsonStr) throws JSONException {
        Log.v(LOG_TAG, "in get review data from json");

        final String RESULTS = "results";
        final String AUTHOR = "author";
        final String CONTENT = "content";
        final String URL = "url";
        final String MOVIE_ID = "id";

        String author;
        String content;
        String url;
        int movieId;

        JSONObject reviewJson = new JSONObject(reviewJsonStr);

        movieId = reviewJson.getInt(MOVIE_ID);
        JSONArray reviewArray = reviewJson.getJSONArray(RESULTS);

        Log.v(LOG_TAG, "num reviews fetched json: " + reviewArray.length());


        // Insert the new review information into the database
        Vector<ContentValues> cVVector = new Vector<ContentValues>(reviewArray.length());

        for(int i = 0; i < reviewArray.length(); i++) {
            JSONObject reviewData = reviewArray.getJSONObject(i);

            author = reviewData.getString(AUTHOR);
            content = reviewData.getString(CONTENT);
            url = reviewData.getString(URL);

            //Log.v(LOG_TAG, "author: " + author + " content: " + content + " url: " + url);

            ContentValues reviewValues = new ContentValues();

            reviewValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, author);
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT, content);
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_URL, url);
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);

            cVVector.add(reviewValues);
            //mReviewArray.add(new Review(author, content, url));
        }

        // add to database
        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            mContext.getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, cvArray);
        }

        Log.v(LOG_TAG, "Fetch review task Complete. " + cVVector.size() + " Inserted.  Movie Id: " + movieId);

    }

    protected Void doInBackground(Integer... params){

        Log.v(LOG_TAG, "in do in background");

        if (params.length == 0) {
            return null;
        }
        int movieId = params[0];

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
                    .appendPath("" + movieId)
                    .appendPath("reviews")
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
            reviewJsonStr = buffer.toString();

            //Log.v(LOG_TAG, reviewJsonStr);

            getReviewDataFromJson(reviewJsonStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } catch(JSONException e)
        {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
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

        return null;
    }
}
