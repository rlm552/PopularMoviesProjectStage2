package com.example.android.popularmovies;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    // String value selected from options menu
    // Initialized to "popular" in onStart method
    private String mOptionSelected;
    GridView gridView;

    static Movie[] objects = new Movie[0];

    static List<Movie> movieObjects = new ArrayList<Movie>(Arrays.asList(objects));

    ImageAdapter imageAdapter = new ImageAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Default option
        mOptionSelected = "popular";

        gridView = (GridView) findViewById(R.id.gridView1);

        gridView.setAdapter(imageAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("Title", movieObjects.get(position).title)
                        .putExtra("Poster_Path", movieObjects.get(position).posterPath)
                        .putExtra("Overview", movieObjects.get(position).overview)
                        .putExtra("Vote_Average", movieObjects.get(position).voteAverage)
                        .putExtra("Release_Date", movieObjects.get(position).releaseDate);

                startActivity(intent);
            }
        });

    }

    public boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }
    @Override
    public void onStart(){
        super.onStart();
        if (isOnline()== false){
            DialogFragment internetConnectivity = new InternetConnectivityFragment();
            internetConnectivity.show(getFragmentManager(), "message");
        }
        else  FetchMovies();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    public void FetchMovies(){
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute();
        gridView.smoothScrollToPosition(1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.top_rated:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                mOptionSelected = "top_rated";
                FetchMovies();
                return true;
            case R.id.most_popular:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                mOptionSelected = "popular";
                FetchMovies();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class FetchMoviesTask extends AsyncTask<Movie, Void, Movie[]>{

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private Movie[] getMovieDataFromJson(String moviesJsonstring)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String RESULTS = "results";
            final String TITLE = "title";
            final String POSTER_PATH = "poster_path";
            final String OVERVIEW = "overview";
            final String VOTE_AVERAGE = "vote_average";
            final String RELEASE_DATE = "release_date";

            JSONObject moviesJson = new JSONObject(moviesJsonstring);
            JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);

            Movie[] movies = new Movie[moviesArray.length()];
            for (int i=0; i < moviesArray.length(); i++)
            {
                try {
                    JSONObject movieObject = moviesArray.getJSONObject(i);

                    movies[i] = new Movie(movieObject.getString(TITLE),movieObject.getString(POSTER_PATH), movieObject.getString(OVERVIEW),
                            movieObject.getString(VOTE_AVERAGE),movieObject.getString(RELEASE_DATE) );
                    //Log.v(LOG_TAG, "Movie poster path: " + movies[i].posterPath);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(),e);
                }
            }

            return movies;

        }

        @Override
        protected Movie[] doInBackground(Movie...params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            try {
                // Construct the URL for the MovieDB query
                // Possible parameters are avaiable at MovieDB's API page, at
                // http://docs.themoviedb.apiary.io/#
                final String MOVIEDB_BASE_URL =
                        "https://api.themoviedb.org/3/movie/" + mOptionSelected;
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                //Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
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
                moviesJsonStr = buffer.toString();

                Log.v(LOG_TAG, "MoviesDB string: " + moviesJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the movie data, there's no point in attempting
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
                return getMovieDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] result) {
            if (result != null) {
                movieObjects.clear();

                for(Movie movie : result) {
                    movieObjects.add(movie);
                }
                imageAdapter.notifyDataSetChanged();
                // New data is back from the server.  Hooray!
            }

        }

    }

}
