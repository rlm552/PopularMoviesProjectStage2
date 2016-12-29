package com.example.android.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReviewDetailActivityFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReviewDetailActivityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReviewDetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    final String LOG_TAG = "REVIEW_ACTIVITY";

    private SimpleCursorAdapter mAdapter;
    private static final int REVIEWS_LOADER = 1;

    private JSONObject mResponse;
    private ListView listView;

    //private OnFragmentInteractionListener mListener;

    public ReviewDetailActivityFragment() {
        // Required empty public constructor
    }

    int movie_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_review_detail_activity, container, false);

        listView = (ListView) rootView.findViewById(R.id.reviews);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Get movie ID from MainActivity to use in movieDB url query
        Bundle extras = getActivity().getIntent().getExtras();
        movie_id = extras.getInt("Movie_ID");

        // Start a new RequestQueue
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        String url = "https://api.themoviedb.org/3/movie/" + movie_id + "/reviews?api_key=3fbd07bcea0160c12263a6f4d32b5ab2";
        Log.v(LOG_TAG, url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        mResponse = response;
                        try {
                            //Parse data from JSONObject response
                            getData();

                            //Column and View to be used in CursorAdapter
                            String[] columns = new String[] {Contracts.ReviewsEntry.COLUMN_REVIEW, Contracts.ReviewsEntry.COLUMN_REVIEW_BUTTON};
                            int[] to = new int[] {R.id.review, R.id.review_button};

                            mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.review, null, columns, to, 0);

                            listView.setAdapter(mAdapter);

                            //Get an instance of the Loader Manager
                            startLoader();
                        }
                        catch (JSONException e){
                            Log.e(LOG_TAG, e.getMessage(),e);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });

        //Add request to queue. If request can be serviced from cache, the cached response is parsed on the cache thread
        queue.add(jsonObjectRequest);
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p/>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }

    public void startLoader(){
        try {
            getLoaderManager().initLoader(REVIEWS_LOADER, null, this);
        }catch (RuntimeException e){
            Log.e(LOG_TAG, "Error initializing loader");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle){

        Uri reviewsUri = Contracts.ReviewsEntry.CONTENT_URI;
        String selection = "movie_id = ?";
        String[] selectionArgs = new String[] {String.valueOf(movie_id)};

        return new CursorLoader(getActivity(),
                reviewsUri,
                null,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, final Cursor cursor) {
        //Swap in cursor for Adapter
        mAdapter.swapCursor(cursor);

        //Set correct height for ListView
        Utils.setListViewHeightBasedOnChildren(listView);

        // Get column number in Reviews table for the "reviews" column
        final int columnIndexKey = cursor.getColumnIndex("url");

        //Find position of clicked item in ListView to find the key to append to a youtube url Intent
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cursor.moveToPosition(position);
                String url = cursor.getString(columnIndexKey);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader){
        mAdapter.swapCursor(null);
    }

    public void getData()
            throws JSONException{
        JSONArray newArray = mResponse.getJSONArray("results");

        String review = "";
        String url;
        ContentValues values = new ContentValues();

        for (int i=0; i < newArray.length(); i++){
            try {
                JSONObject Object = newArray.getJSONObject(i);

                //Check to see if reviews for movie already exist in Reviews Table
                if (i==0){


                    String selection = "movie_id = ?";
                    String[] selectionArgs = new String[] {String.valueOf(movie_id)};

                    Cursor reviewsCursor = getActivity().getContentResolver().query(
                            Contracts.ReviewsEntry.CONTENT_URI,
                            null,
                            selection,
                            selectionArgs,
                            null
                    );
                    if (reviewsCursor.getCount() == 0){
                        reviewsCursor.close();
                    }else {
                        reviewsCursor.close();
                        return;
                    }
                }

                review = Object.getString("content");
                if (review.length()>200) {review = review.substring(0,200) + "...";}

                url = Object.getString("url");

                //Clear values so we don't readd the same values
                values.clear();

                values.put(Contracts.ReviewsEntry.COLUMN_MOVIE_ID, movie_id);
                values.put(Contracts.ReviewsEntry.COLUMN_REVIEW, review);
                values.put(Contracts.ReviewsEntry.COLUMN_URL, url);
                values.put(Contracts.ReviewsEntry.COLUMN_REVIEW_BUTTON, "READ MORE");

                ContentResolver resolver = getActivity().getContentResolver();

                //Insert Values into Reviews Database
                resolver.insert(Contracts.ReviewsEntry.CONTENT_URI, values);

            }
            catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(),e);
            }
        }
    }
}
