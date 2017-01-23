package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Rory on 11/26/2016.
 */
class DbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 11;

    static final String DATABASE_NAME = "movies.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold trailers.
        final String SQL_CREATE_TRAILERS_TABLE = "CREATE TABLE " + Contracts.TrailersEntry.TABLE_NAME + " (" +
                Contracts.TrailersEntry._ID + " INTEGER PRIMARY KEY," +
                Contracts.TrailersEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                Contracts.TrailersEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                Contracts.TrailersEntry.COLUMN_KEY + " TEXT NOT NULL" +
                " );";

        // Create a table to hold reviews.
        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + Contracts.ReviewsEntry.TABLE_NAME + " (" +
                Contracts.ReviewsEntry._ID + " INTEGER PRIMARY KEY," +
                Contracts.ReviewsEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                Contracts.ReviewsEntry.COLUMN_REVIEW + " TEXT NOT NULL, " +
                Contracts.ReviewsEntry.COLUMN_URL + " TEXT NOT NULL, " +
                Contracts.ReviewsEntry.COLUMN_REVIEW_BUTTON + " TEXT NOT NULL" +
                " );";

        // Create a table to hold favorites.
        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + Contracts.FavoritesEntry.TABLE_NAME + " (" +
                Contracts.FavoritesEntry._ID + " INTEGER PRIMARY KEY," +
                Contracts.FavoritesEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL , " +
                Contracts.FavoritesEntry.COLUMN_TITLE + " TEXT NOT NULL , " +
                Contracts.FavoritesEntry.COLUMN_POSTER_PATH + " TEXT UNIQUE NOT NULL , " +
                Contracts.FavoritesEntry.COLUMN_OVERVIEW + " TEXT UNIQUE NOT NULL , " +
                Contracts.FavoritesEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL , " +
                Contracts.FavoritesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL " +
                " );";


        sqLiteDatabase.execSQL(SQL_CREATE_TRAILERS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEWS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Contracts.TrailersEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Contracts.ReviewsEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Contracts.FavoritesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
