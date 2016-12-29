package com.example.android.popularmovies.data;

import android.provider.BaseColumns;

/**
 *
 * Defines table and column names for the favorite database.
 */

public class FavoritesContract {

    /* Inner class that defines the contents of the favorites table */
    public static final class FavoritesEntry implements BaseColumns {

        public static final String TABLE_NAME = "Favorites";

        // Movie title, stored as String
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_RELEASE_DATE = "release_date";
    }

}
