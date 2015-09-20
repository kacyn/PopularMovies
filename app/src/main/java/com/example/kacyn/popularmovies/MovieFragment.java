package com.example.kacyn.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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
public class MovieFragment extends Fragment {
    private ImageAdapter mMovieAdapter;
    private int mNumMoviesFetched = 15;
    private ArrayList<Movie> movieArray = new ArrayList<Movie>();
    private ArrayList<String> urlArray = new ArrayList<String>();

    public MovieFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mMovieAdapter = new ImageAdapter(getActivity(), mNumMoviesFetched, urlArray);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(mMovieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent detailIntent = new Intent(getActivity(), DetailActivity.class);

                //launch detail view
                detailIntent.putExtra("MovieIntent", movieArray.get(i));
                startActivity(detailIntent);
            }
        });

        return rootView;
    }

    private void updateMovieData(){
        FetchMovieTask fetchMovieTask = new FetchMovieTask();

        //retrieve sort preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortCriterion = sharedPreferences.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));

        fetchMovieTask.execute(sortCriterion);
    }

    @Override
    public void onStart(){
        super.onStart();
        updateMovieData();
    }

    public class FetchMovieTask extends AsyncTask<String, String, Movie[]> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        protected Movie[] doInBackground(String... params){
            String criterion = "";
            String order = "desc";
            int minVotes = 50;
            String apiKey = 

            if(params.length > 0){
                criterion = params[0];
            }

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
                        .appendQueryParameter("api_key", apiKey);

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
                return getMovieDataFromJson(movieJsonStr, mNumMoviesFetched);
            }
            catch(JSONException e)
            {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Movie[] result) {
            if(result != null) {
                ArrayList<String> urls = new ArrayList<String>();

                //clear previous data
                movieArray.clear();

                //add new information
                for(int i = 0; i < result.length; i++){
                    urls.add(result[i].mPosterUrl);
                    movieArray.add(result[i]);
                }

                //update view
                mMovieAdapter.update(urls);
            }
        }

        private Movie[] getMovieDataFromJson(String movieJsonStr, int numMovies) throws JSONException {
            final String RESULTS = "results";
            final String TITLE = "original_title";
            final String RELEASE_DATE = "release_date";
            final String VOTE_AVG = "vote_average";
            final String SYNOPSIS = "overview";
            final String POSTER_URL = "poster_path";

            String title;
            String releaseDate;
            double voteAvg;
            String synopsis;
            String posterUrl;

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(RESULTS);

            Movie[] results = new Movie[numMovies];

            for(int i = 0; i < numMovies; i++) {
                // Get the JSON object representing the day
                JSONObject movieData = movieArray.getJSONObject(i);

                title = movieData.getString(TITLE);
                releaseDate = movieData.getString(RELEASE_DATE);
                voteAvg = movieData.getDouble(VOTE_AVG);
                synopsis = movieData.getString(SYNOPSIS);
                posterUrl = "http://image.tmdb.org/t/p/w342" + movieData.getString(POSTER_URL);

                results[i] = new Movie(title, releaseDate, voteAvg, synopsis, posterUrl);
            }
            return results;
        }
    }
}
