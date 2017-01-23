package com.example.android.popularmovies;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android.popularmovies.data.Contracts;
import com.squareup.picasso.Picasso;

public class DetailFragment extends Fragment {

    private int mMovieId;
    private String mTitle;
    private String mPosterPath;
    private String mOverview;
    private String mVoteAverage;
    private String mReleaseDate;

    public DetailFragment() {

    }

    private interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        Bundle extras = getActivity().getIntent().getExtras();

        if (bundle != null) {
            mMovieId = bundle.getInt("Movie_ID");
            mTitle = bundle.getString("Title");
            mPosterPath = bundle.getString("Poster_Path");
            mOverview = bundle.getString("Overview");
            mVoteAverage = bundle.getString("Vote_Average");
            mReleaseDate = bundle.getString("Release_Date").substring(0,4);
            }
        else if(extras != null) {
            mMovieId = extras.getInt("Movie_ID");
            mTitle = extras.getString("Title");
            mPosterPath = extras.getString("Poster_Path");
            mOverview = extras.getString("Overview");
            mVoteAverage = extras.getString("Vote_Average");
            mReleaseDate = extras.getString("Release_Date").substring(0, 4);
        } else{
            //Placeholder data
            mMovieId = 0;
            mTitle = null;
            mPosterPath = null;
            mOverview = "Click Movie Poster for a more information.";
            mVoteAverage = null;
            mReleaseDate = null;
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        TextView textView = (TextView) rootView.findViewById(R.id.movie_title);
        textView.setText(mTitle);

        ImageView imageView = (ImageView) rootView.findViewById(R.id.movie_poster);
        if(mPosterPath != null) Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w185/" + mPosterPath).into(imageView);

        textView = (TextView) rootView.findViewById(R.id.movie_overview);
        textView.setText(mOverview);

        textView = (TextView) rootView.findViewById(R.id.movie_vote_average);
        textView.setText("Vote Average: " + mVoteAverage);

        textView = (TextView) rootView.findViewById(R.id.movie_release_date);
        textView.setText(mReleaseDate);

        final ImageButton imageButton = (ImageButton) rootView.findViewById(R.id.favoriteButton);

        Boolean isFavorite = readFavoritesDb(mTitle);
        if(isFavorite){
            imageButton.setTag(R.drawable.ic_favorite_black_18dp);
            imageButton.setImageResource(R.drawable.ic_favorite_black_18dp);
        }
        else{
            imageButton.setTag(R.drawable.ic_favorite_border_black_18dp);
            imageButton.setImageResource(R.drawable.ic_favorite_border_black_18dp);
        }

        imageButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                toggleFavoriteButton(imageButton, mMovieId, mTitle, mPosterPath,
                        mOverview, mVoteAverage, mReleaseDate);
            }
        });



        return rootView;
    }

    private boolean readFavoritesDb(String title){

        Cursor cursor = getActivity().getContentResolver().query(
                Contracts.FavoritesEntry.CONTENT_URI,  // Table to Query
                null,
                null,
                null,
                null
        );

        // Go to the first row
        cursor.moveToFirst();

        final int columnTitle = cursor.getColumnIndex("title");

        // Iterate through every row, compare favorite in database to title in view
        for (int i = 0; i < cursor.getCount(); i++) {
            String favorite = cursor.getString(columnTitle);
            if (favorite.equals(title)){
                cursor.close();
                return true;
            }
            //Move to next row
            cursor.moveToNext();
        }

        cursor.close();
        return false;
    }

    private void toggleFavoriteButton(ImageButton imageButton, int movie_id, String title, String posterPath,
                                      String overview, String voteAverage, String releaseDate){

        int xOffset =  (int) Math.floor(imageButton.getX());
        int yOffset =  (int) Math.floor(imageButton.getY());

        Integer resource = (Integer) imageButton.getTag();

        if (resource == R.drawable.ic_favorite_black_18dp){
            imageButton.setImageResource(R.drawable.ic_favorite_border_black_18dp);
            imageButton.setTag(R.drawable.ic_favorite_border_black_18dp);

            removeFavorites(title);

            Toast toast = Toast.makeText(getActivity(), "Removed from favorites", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, xOffset, yOffset);
            toast.show();
        }
        else if (resource == R.drawable.ic_favorite_border_black_18dp){
            imageButton.setImageResource(R.drawable.ic_favorite_black_18dp);
            imageButton.setTag(R.drawable.ic_favorite_black_18dp);

            insertFavorites(movie_id, title, posterPath, overview, voteAverage, releaseDate);

            Toast toast = Toast.makeText(getActivity(), "Added to favorites", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, xOffset, yOffset);
            toast.show();
        }
    }

    private void insertFavorites(int movie_id, String title, String posterPath, String overview, String voteAverage,
                                 String releaseDate){

        ContentValues values = new ContentValues();
        values.put(Contracts.FavoritesEntry.COLUMN_MOVIE_ID, movie_id);
        values.put(Contracts.FavoritesEntry.COLUMN_TITLE, title);
        values.put(Contracts.FavoritesEntry.COLUMN_POSTER_PATH, posterPath);
        values.put(Contracts.FavoritesEntry.COLUMN_OVERVIEW, overview);
        values.put(Contracts.FavoritesEntry.COLUMN_VOTE_AVERAGE, voteAverage);
        values.put(Contracts.FavoritesEntry.COLUMN_RELEASE_DATE, releaseDate);

        ContentResolver resolver = getActivity().getContentResolver();

        //Insert Values into Favorites Database
        resolver.insert(Contracts.FavoritesEntry.CONTENT_URI, values);
    }

    private void removeFavorites(String title){

        // Define 'where' part of query.
        String selection = Contracts.FavoritesEntry.COLUMN_TITLE + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { title };

        ContentResolver resolver = getActivity().getContentResolver();

        //Insert Values into Trailers Database
        resolver.delete(Contracts.FavoritesEntry.CONTENT_URI, selection, selectionArgs);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        OnFragmentInteractionListener mListener = null;
    }

}
