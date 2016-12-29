package com.example.android.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
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
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;
import android.support.v4.app.Fragment;

public class TrailerDetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    final String LOG_TAG = "TRAILERS";

    private SimpleCursorAdapter mAdapter;
    private static final int TRAILER_LOADER = 0;

    private JSONObject mResponse;
    private ListView listView;



    //public OnFragmentInteractionListener mListener;

    public TrailerDetailActivityFragment(){
        // Required empty public constructor
    }

    int movie_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        final View rootView = inflater.inflate(R.layout.trailer_fragment_detail, container, false);

        listView = (ListView) rootView.findViewById(R.id.trailers);

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

        String url = "https://api.themoviedb.org/3/movie/" + movie_id + "/videos?api_key=3fbd07bcea0160c12263a6f4d32b5ab2";

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
                            String[] columns = new String[] {Contracts.TrailersEntry.COLUMN_NAME};
                            int[] to = new int[] {R.id.trailer_button};

                            mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.trailer_button_layout, null, columns, to, 0);

                            listView.setAdapter(mAdapter);

                            //Get and instance of the Loader Manager
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

    public void startLoader(){
        try {
            getLoaderManager().initLoader(TRAILER_LOADER, null, this);
        }catch (RuntimeException e){
            Log.e(LOG_TAG, "Error initializing loader");
        }
    }

    @Override
    public  Loader<Cursor> onCreateLoader(int i, Bundle bundle){

        Uri trailersUri = Contracts.TrailersEntry.CONTENT_URI;
        String selection = "movie_id = ?";
        String[] selectionArgs = new String[] {String.valueOf(movie_id)};

        return new CursorLoader(getActivity(),
                trailersUri,
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

        // Get column number in Trailers table for the "key" column
        final int columnIndexKey = cursor.getColumnIndex("key");

        //Find position of clicked item in ListView to find the key to append to a youtube url Intent
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cursor.moveToPosition(position);
                String key = cursor.getString(columnIndexKey);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + key)));
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
        String name;
       String key;

       ContentValues values = new ContentValues();

    for (int i=0; i < newArray.length(); i++){
        try {
            JSONObject Object = newArray.getJSONObject(i);

            //Check to see if trailers for movie already exist in Trailers Table
            if (i==0){

                String selection = "movie_id = ?";
                String[] selectionArgs = new String[] {String.valueOf(movie_id)};

                Cursor trailersCursor = getActivity().getContentResolver().query(
                        Contracts.TrailersEntry.CONTENT_URI,
                        null,
                        selection,
                        selectionArgs,
                        null
                );
                if (trailersCursor.getCount() == 0){
                    trailersCursor.close();
                }else {
                    trailersCursor.close();
                    return;
                }
            }

            name = Object.getString("name");
            key = Object.getString("key");

            //Clear values so we don't readd the same values
            values.clear();

            values.put(Contracts.TrailersEntry.COLUMN_MOVIE_ID, movie_id);
            values.put(Contracts.TrailersEntry.COLUMN_NAME, name);
            values.put(Contracts.TrailersEntry.COLUMN_KEY, key);

            ContentResolver resolver = getActivity().getContentResolver();

            //Insert Values into Trailers Database
            resolver.insert(Contracts.TrailersEntry.CONTENT_URI, values);

        }
        catch (JSONException e){
            Log.e(LOG_TAG, e.getMessage(),e);
        }
    }

}

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

}

