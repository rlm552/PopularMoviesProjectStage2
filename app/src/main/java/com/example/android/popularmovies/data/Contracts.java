package com.example.android.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Rory on 11/26/2016.
 */
public class Contracts {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)

    public static final String PATH_TRAILERS = "trailers";
    public static final String PATH_REVIEWS = "reviews";
    public static final String PATH_FAVORITES = "favorites";

    /* Inner class that defines the table contents of the trailers table */
    public static final class TrailersEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILERS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILERS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILERS;

        // Table name
        public static final String TABLE_NAME = "Trailers";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_NAME = "name";

        public static final String COLUMN_KEY = "key";

        public static Uri buildTrailersUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /* Inner class that defines the table contents of the reviews table */
    public static final class ReviewsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;

        // Table name
        public static final String TABLE_NAME = "Reviews";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_REVIEW = "review";

        public static final String COLUMN_URL = "url";

        public static final String COLUMN_REVIEW_BUTTON = "review_button";

        public static Uri buildReviewsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /* Inner class that defines the contents of the favorites table */
    public static final class FavoritesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;

        // Table name
        public static final String TABLE_NAME = "Favorites";

        // Movie title, stored as String
        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_POSTER_PATH = "poster_path";

        public static final String COLUMN_OVERVIEW = "overview";

        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static Uri buildFavoritesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
