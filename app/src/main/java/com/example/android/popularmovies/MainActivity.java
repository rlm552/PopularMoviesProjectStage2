package com.example.android.popularmovies;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
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
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.popularmovies.data.FavoritesContract;
import com.example.android.popularmovies.data.FavoritesDBHelper;
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
    private static final String STATE_OPTION = "option";
    private final String LOG_TAG = MainActivity.this.getClass().getSimpleName();

    private int mCounter;
    // String value selected from options menu
    // Initialized to "popular" in onStart method
    private String mOptionSelected = "popular";
    GridView gridView;

    Movie[] objects = new Movie[0];

    List<Movie> movieObjects = new ArrayList<Movie>(Arrays.asList(objects));

    ImageAdapter imageAdapter = new ImageAdapter(this, movieObjects);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Clear databases//
//        TrailersDBHelper dbHelper = new TrailersDBHelper(this);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//
//        db.delete(TrailersContract.TrailersEntry.TABLE_NAME, null, null);
//        db.close();

        setContentView(R.layout.activity_main);

        gridView = (GridView) findViewById(R.id.gridView1);

        gridView.setAdapter(imageAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("Movie_ID", movieObjects.get(position).movieID)
                        .putExtra("Title", movieObjects.get(position).title)
                        .putExtra("Poster_Path", movieObjects.get(position).posterPath)
                        .putExtra("Overview", movieObjects.get(position).overview)
                        .putExtra("Vote_Average", movieObjects.get(position).voteAverage)
                        .putExtra("Release_Date", movieObjects.get(position).releaseDate);

                startActivity(intent);
            }
        });

        // If we have a saved state then we can restore it now
        if (savedInstanceState != null) {
            mOptionSelected = savedInstanceState.getString(STATE_OPTION, "popular");
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_OPTION, mOptionSelected);
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
        else if (mOptionSelected == "favorite") {
            returnFavorites();
        }
        else FetchMovies();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        returnFavorites();
        return true;
    }

    public void FetchMovies(){
       //FetchMoviesTask moviesTask = new FetchMoviesTask();
       //moviesTask.execute();
        final String MOVIEDB_BASE_URL =
                "https://api.themoviedb.org/3/movie/" + mOptionSelected;
        final String APPID_PARAM = "api_key";

        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                .appendQueryParameter(APPID_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                .build();

        String url = builtUri.toString();

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsArrRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            getData(response);
                        }
                        catch (JSONException e){
                            Log.e(LOG_TAG, e.getMessage(),e);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Toast toast = Toast.makeText(getBaseContext(), "Error retrieving data. Please check Internet Connection and try again!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });

        // Access the RequestQueue through your singleton class.
        //MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
        queue.add(jsArrRequest);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.favorite_option:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                mOptionSelected = "favorite";
                returnFavorites();
                gridView.smoothScrollToPosition(1);
                return true;
            case R.id.top_rated:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                mOptionSelected = "top_rated";
                FetchMovies();
                gridView.smoothScrollToPosition(1);
                return true;
            case R.id.popular:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                mOptionSelected = "popular";
                FetchMovies();
                gridView.smoothScrollToPosition(1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void returnFavorites(){

        int columnMovieID = 1;
        int columnTitle = 2;
        int columnPosterPath = 3;
        int columnOverview = 4;
        int columnVoteAverage = 5;
        int columnReleaseDate = 6;

        SQLiteDatabase db = new FavoritesDBHelper(
                this).getWritableDatabase();

        Cursor cursor = db.query(
                FavoritesContract.FavoritesEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        movieObjects.clear();
        // Go to the first row
        cursor.moveToFirst();

        // Iterate through every row
        for (int i = 0; i < cursor.getCount(); i++) {
            Movie movie = new Movie(cursor.getInt(columnMovieID), cursor.getString(columnTitle),cursor.getString(columnPosterPath),
                    cursor.getString(columnOverview), cursor.getString(columnVoteAverage),cursor.getString(columnReleaseDate) );
            movieObjects.add(movie);

            //Move to next row
            cursor.moveToNext();
        }

        cursor.close();
        db.close();

        imageAdapter.notifyDataSetChanged();
        return;

    }

    public void getData(JSONObject response)
            throws JSONException{

        // These are the names of the JSON objects that need to be extracted.
        final String RESULTS = "results";
        final String MOVIE_ID = "id";
        final String TITLE = "title";
        final String POSTER_PATH = "poster_path";
        final String OVERVIEW = "overview";
        final String VOTE_AVERAGE = "vote_average";
        final String RELEASE_DATE = "release_date";

        JSONArray moviesArray = response.getJSONArray(RESULTS);
        Movie[] movies = new Movie[moviesArray.length()];

        for (int i=0; i < moviesArray.length(); i++){
            try {
                JSONObject movieObject = moviesArray.getJSONObject(i);
                movies[i] = new Movie(movieObject.getInt(MOVIE_ID), movieObject.getString(TITLE),movieObject.getString(POSTER_PATH), movieObject.getString(OVERVIEW),
                        movieObject.getString(VOTE_AVERAGE),movieObject.getString(RELEASE_DATE) );
            }
            catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(),e);
            }
        }
        if (movies != null) {
            movieObjects.clear();

            for(Movie movie : movies) {
                movieObjects.add(movie);
            }
            imageAdapter.notifyDataSetChanged();
            // New data is back from the server.  Hooray!
        }
    }
}
