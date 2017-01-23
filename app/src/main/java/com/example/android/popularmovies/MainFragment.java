package com.example.android.popularmovies;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.popularmovies.data.Contracts;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class MainFragment extends Fragment {
    private final String LOG_TAG = "MainFragment";

    private OnFragmentInteractionListener mListener;

    private static final String STATE_OPTION = "option";

    // String value selected from options menu
    // Initialized to "popular" in onStart method
    private String mOptionSelected = "popular";
    private GridView gridView;

    private Movie[] objects = new Movie[0];

    private List<Movie> movieObjects = new ArrayList<>(Arrays.asList(objects));

    private ImageAdapter imageAdapter;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;

        //If in two-pane mode the MainFragment GridView is half the screenwidth
        if (getResources().getBoolean(R.bool.twoPaneMode)){
            imageAdapter = new ImageAdapter(getActivity(), movieObjects,screenWidth/2);
        } else {
            imageAdapter = new ImageAdapter(getActivity(), movieObjects, screenWidth);
        }

        if (Utils.isOnline()== false){
            Toast toast = Toast.makeText(getActivity(), "Cannot connect to Internet. Please try again", Toast.LENGTH_LONG);
            toast.show();
        }
        else if (mOptionSelected.equals("favorite") ) {
            returnFavorites();
        }
        else FetchMovies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        gridView = (GridView) rootView.findViewById(R.id.gridView1);

        gridView.setAdapter(imageAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                if(getResources().getBoolean(R.bool.twoPaneMode)){
                    Bundle args = new Bundle();
                    args.putInt("Movie_ID", movieObjects.get(position).movieID);
                    args.putString("Title", movieObjects.get(position).title);
                    args.putString("Poster_Path", movieObjects.get(position).posterPath);
                    args.putString("Overview", movieObjects.get(position).overview);
                    args.putString("Vote_Average", movieObjects.get(position).voteAverage);
                    args.putString("Release_Date", movieObjects.get(position).releaseDate);

                    mListener.onFragmentInteraction(args, false);
                }else {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra("Movie_ID", movieObjects.get(position).movieID)
                            .putExtra("Title", movieObjects.get(position).title)
                            .putExtra("Poster_Path", movieObjects.get(position).posterPath)
                            .putExtra("Overview", movieObjects.get(position).overview)
                            .putExtra("Vote_Average", movieObjects.get(position).voteAverage)
                            .putExtra("Release_Date", movieObjects.get(position).releaseDate);

                    startActivity(intent);
                }
            }
        });




        // If we have a saved state then we can restore it now
        if (savedInstanceState != null) {
            mOptionSelected = savedInstanceState.getString(STATE_OPTION, "popular");
        }

        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_OPTION, mOptionSelected);
    }

    private void FetchMovies(){
        //FetchMoviesTask moviesTask = new FetchMoviesTask();
        //moviesTask.execute();
        final String MOVIEDB_BASE_URL =
                "https://api.themoviedb.org/3/movie/" + mOptionSelected;
        final String APPID_PARAM = "api_key";

        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                .appendQueryParameter(APPID_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                .build();

        String url = builtUri.toString();

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        JsonObjectRequest jsArrRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            getData(response);
                        }
                        catch (JSONException e){
                            Log.e(LOG_TAG, e.getMessage(), e);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(getContext(), "Error retrieving data. Please check Internet Connection and try again!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });

        // Access the RequestQueue through your singleton class.
        //MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
        queue.add(jsArrRequest);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //This bit of code makes sure that when the GridView changes the details view in two pane mode is
        // cleared
        if(getResources().getBoolean(R.bool.twoPaneMode)) {
            mListener.onFragmentInteraction(null, true);
        }

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

    private void returnFavorites(){

        Cursor cursor = getActivity().getContentResolver().query(
                Contracts.FavoritesEntry.CONTENT_URI,  // Table to Query
                null,
                null,
                null,
                null
        );

        int columnMovieID = cursor.getColumnIndex("movie_id");
        int columnTitle = cursor.getColumnIndex("title");
        int columnPosterPath = cursor.getColumnIndex("poster_path");
        int columnOverview = cursor.getColumnIndex("overview");
        int columnVoteAverage = cursor.getColumnIndex("vote_average");
        int columnReleaseDate = cursor.getColumnIndex("release_date");

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

        imageAdapter.notifyDataSetChanged();
    }

    private void getData(JSONObject response)
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

            Collections.addAll(movieObjects, movies);
            imageAdapter.notifyDataSetChanged();
            // New data is back from the server.  Hooray!
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Bundle bundle, boolean dataSetChanged);
    }
}
