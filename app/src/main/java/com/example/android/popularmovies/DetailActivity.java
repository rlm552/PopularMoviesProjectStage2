package com.example.android.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.android.popularmovies.data.FavoritesContract;
import com.example.android.popularmovies.data.FavoritesDBHelper;


public class DetailActivity extends AppCompatActivity {

    static final String LOG_TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageButton imageButton = (ImageButton) findViewById(R.id.favoriteButton);

        Boolean isFavorite = readFavoritesDb();
        if(isFavorite){
            imageButton.setTag(R.drawable.ic_favorite_black_18dp);
            imageButton.setImageResource(R.drawable.ic_favorite_black_18dp);
        }
        else{
            imageButton.setTag(R.drawable.ic_favorite_border_black_18dp);
            imageButton.setImageResource(R.drawable.ic_favorite_border_black_18dp);
        }
    }

    public void toggleFavoriteButton(View view){

        ImageButton imageButton = (ImageButton) findViewById(R.id.favoriteButton);
        int xOffset =  (int) Math.floor(imageButton.getX());
        int yOffset =  (int) Math.floor(imageButton.getY());

        Integer resource = (Integer) imageButton.getTag();

        if (resource == R.drawable.ic_favorite_black_18dp){
            imageButton.setImageResource(R.drawable.ic_favorite_border_black_18dp);
            imageButton.setTag(R.drawable.ic_favorite_border_black_18dp);

            removeFavorites();

            Toast toast = Toast.makeText(this, "Removed from favorites", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, xOffset, yOffset);
            toast.show();
        }
        else if (resource == R.drawable.ic_favorite_border_black_18dp){
            imageButton.setImageResource(R.drawable.ic_favorite_black_18dp);
            imageButton.setTag(R.drawable.ic_favorite_black_18dp);

            insertFavorites();

            Toast toast = Toast.makeText(this, "Added to favorites", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, xOffset, yOffset);
            toast.show();
        }

    }

    public long insertFavorites(){

        Bundle extras = this.getIntent().getExtras();
        final int movie_id = extras.getInt("Movie_ID");
        final String title = extras.getString("Title");
        final String posterPath = extras.getString("Poster_Path");
        final String overview = extras.getString("Overview");
        final String voteAverage = extras.getString("Vote_Average");
        final String releaseDate = extras.getString("Release_Date").substring(0, 4);

        SQLiteDatabase db = new FavoritesDBHelper(
                this).getWritableDatabase();

        ContentValues favoriteValues = new ContentValues();
        favoriteValues.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID, movie_id);
        favoriteValues.put(FavoritesContract.FavoritesEntry.COLUMN_TITLE, title);
        favoriteValues.put(FavoritesContract.FavoritesEntry.COLUMN_POSTER_PATH, posterPath);
        favoriteValues.put(FavoritesContract.FavoritesEntry.COLUMN_OVERVIEW, overview);
        favoriteValues.put(FavoritesContract.FavoritesEntry.COLUMN_VOTE_AVERAGE, voteAverage);
        favoriteValues.put(FavoritesContract.FavoritesEntry.COLUMN_RELEASE_DATE, releaseDate);

        long locationRowId;
        locationRowId = db.insert(FavoritesContract.FavoritesEntry.TABLE_NAME, null, favoriteValues);

        //cursor.close();
        db.close();
        return locationRowId;
    }

    public void removeFavorites(){

        Bundle extras = this.getIntent().getExtras();
        final String title = extras.getString("Title");

        SQLiteDatabase db = new FavoritesDBHelper(
                this).getWritableDatabase();

        // Define 'where' part of query.
        String selection = FavoritesContract.FavoritesEntry.COLUMN_TITLE + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { title };
        // Issue SQL statement.
        db.delete(FavoritesContract.FavoritesEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
    }

    public boolean readFavoritesDb(){

        Bundle extras = this.getIntent().getExtras();
        final String title = extras.getString("Title");

        int columnTitle = 2;

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

        // Go to the first row
        cursor.moveToFirst();

        // Iterate through every row, compare favorite in database to title in view
        for (int i = 0; i < cursor.getCount(); i++) {
            String favorite = cursor.getString(columnTitle);
            if (favorite.equals(title)){
                cursor.close();
                db.close();
                return true;
            }
            //Move to next row
            cursor.moveToNext();
        }

        cursor.close();
        db.close();
        return false;
    }


}

