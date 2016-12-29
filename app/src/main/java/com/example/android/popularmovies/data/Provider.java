package com.example.android.popularmovies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Rory on 11/26/2016.
 */
public class Provider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DbHelper mOpenHelper;

    static final int TRAILERS = 100;
    static final int REVIEWS = 200;

    private static final SQLiteQueryBuilder sTrailersQueryBuilder;
    private static final SQLiteQueryBuilder sReviewsQueryBuilder;

    static{
        sTrailersQueryBuilder = new SQLiteQueryBuilder();
        sTrailersQueryBuilder.setTables(Contracts.TrailersEntry.TABLE_NAME);
    }

    static{
        sReviewsQueryBuilder = new SQLiteQueryBuilder();
        sReviewsQueryBuilder.setTables(Contracts.ReviewsEntry.TABLE_NAME);
    }

    //Trailers.trailers_setting = ?
    private static final String sTrailersSelection =
            Contracts.TrailersEntry.TABLE_NAME+
                    "." + Contracts.TrailersEntry.COLUMN_NAME + " = ? ";

    private static final String sReviewsSelection =
            Contracts.ReviewsEntry.TABLE_NAME+
                    "." + Contracts.ReviewsEntry.COLUMN_REVIEW + " = ? ";


    private Cursor getTrailer(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        return sTrailersQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getReview(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        return sReviewsQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }


    static UriMatcher buildUriMatcher() {

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = Contracts.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, Contracts.PATH_TRAILERS, TRAILERS);
        matcher.addURI(authority, Contracts.PATH_REVIEWS, REVIEWS);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case TRAILERS:
                return Contracts.TrailersEntry.CONTENT_TYPE;
            case REVIEWS:
                return Contracts.ReviewsEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case TRAILERS:
            {
                retCursor = getTrailer(uri, projection, selection, selectionArgs, sortOrder);
                break;
            }
            case REVIEWS:
            {
                retCursor = getReview(uri, projection, selection, selectionArgs, sortOrder);
                break;
            }
          default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case TRAILERS: {
                long _id = db.insert(Contracts.TrailersEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = Contracts.TrailersEntry.buildTrailersUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REVIEWS: {
                long _id = db.insert(Contracts.ReviewsEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = Contracts.ReviewsEntry.buildReviewsUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Log.v("Provider delete", "" + match);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case TRAILERS:
                rowsDeleted = db.delete(
                        Contracts.TrailersEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEWS:
                rowsDeleted = db.delete(
                        Contracts.ReviewsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

   @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case TRAILERS:
                rowsUpdated = db.update(Contracts.TrailersEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case REVIEWS:
                rowsUpdated = db.update(Contracts.ReviewsEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}