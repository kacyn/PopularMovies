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

public class FetchTrailerTask extends AsyncTask<Integer, Void, Void> {

    private final String LOG_TAG = FetchTrailerTask.class.getSimpleName();

    private final Context mContext;

    public FetchTrailerTask(Context context) {
        Log.v(LOG_TAG, "in constructor ");

        mContext = context;
    }

   /* @Override
    protected void onPostExecute(Void aVoid) {
        //update view
        mTrailerAdapter.update(mNumTrailersFetched, mTrailerUrlArray);
    }*/

    private void getTrailerDataFromJson(String trailerJsonStr) throws JSONException {
        //mTrailerUrlArray.clear();

        final String RESULTS = "results";
        final String KEY = "key";
        final String MOVIE_ID = "id";

        String key;
        int movieId;

        JSONObject trailerJson = new JSONObject(trailerJsonStr);

        movieId = trailerJson.getInt(MOVIE_ID);
        JSONArray trailerArray = trailerJson.getJSONArray(RESULTS);

        // Insert the new review information into the database
        Vector<ContentValues> cVVector = new Vector<ContentValues>(trailerArray.length());

        Log.v("Trailer Adapter", "num trailers fetched json: " + trailerArray.length());

        for(int i = 0; i < trailerArray.length(); i++) {
            JSONObject reviewData = trailerArray.getJSONObject(i);

            key = reviewData.getString(KEY);

            ContentValues trailerValues = new ContentValues();

            trailerValues.put(MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY, key);
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movieId);

            cVVector.add(trailerValues);
            //mTrailerUrlArray.add(key);
        }

        // add to database
        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            mContext.getContentResolver().bulkInsert(MovieContract.TrailerEntry.CONTENT_URI, cvArray);
        }

        Log.v(LOG_TAG, "Fetch trailer task Complete. " + cVVector.size() + " Inserted");

    }

    protected Void doInBackground(Integer... params){
        if (params.length == 0) {
            return null;
        }
        int movieId = params[0];

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
                    .appendPath("" + movieId)
                    .appendPath("videos")
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
            trailerJsonStr = buffer.toString();
            getTrailerDataFromJson(trailerJsonStr);

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
}