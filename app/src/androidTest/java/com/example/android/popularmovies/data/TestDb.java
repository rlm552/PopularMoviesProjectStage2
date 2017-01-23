package com.example.android.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import java.util.HashSet;

/**
 * Created by Rory on 12/9/2016.
 */
public class TestDb extends AndroidTestCase {
    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    private void deleteTheDatabase() {
        mContext.deleteDatabase(DbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(Contracts.TrailersEntry.TABLE_NAME);
        tableNameHashSet.add(Contracts.ReviewsEntry.TABLE_NAME);
        tableNameHashSet.add(Contracts.FavoritesEntry.TABLE_NAME);

        //Trailers Db
        mContext.deleteDatabase(DbHelper.DATABASE_NAME);
        SQLiteDatabase db = new DbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't have a trailers entry or reviews entry table
        assertTrue("Error: Your database was created without trailers entry, reviews entry table, or favorites entry",
                tableNameHashSet.isEmpty());

        // now, does our trailers table contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + Contracts.TrailersEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for trailers table information.",
                c.moveToFirst());

        // Build a HashSet of all of the trailers column names we want to look for
        final HashSet<String> trailersColumnHashSet = new HashSet<>();
        trailersColumnHashSet.add(Contracts.TrailersEntry._ID);
        trailersColumnHashSet.add(Contracts.TrailersEntry.COLUMN_MOVIE_ID);
        trailersColumnHashSet.add(Contracts.TrailersEntry.COLUMN_NAME);
        trailersColumnHashSet.add(Contracts.TrailersEntry.COLUMN_KEY);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            trailersColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // now, does our reviews table contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + Contracts.ReviewsEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for reviews table information.",
                c.moveToFirst());

        // Build a HashSet of all of the reviews column names we want to look for
        final HashSet<String> reviewsColumnHashSet = new HashSet<>();
        reviewsColumnHashSet.add(Contracts.ReviewsEntry._ID);
        reviewsColumnHashSet.add(Contracts.ReviewsEntry.COLUMN_MOVIE_ID);
        reviewsColumnHashSet.add(Contracts.ReviewsEntry.COLUMN_REVIEW);
        reviewsColumnHashSet.add(Contracts.ReviewsEntry.COLUMN_URL);
        reviewsColumnHashSet.add(Contracts.ReviewsEntry.COLUMN_REVIEW_BUTTON);

        do {
            String columnName = c.getString(columnNameIndex);
            reviewsColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // now, does our favorites table contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + Contracts.FavoritesEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for favorites table information.",
                c.moveToFirst());

        // Build a HashSet of all of the reviews column names we want to look for
        final HashSet<String> favoritesColumnHashSet = new HashSet<>();
        favoritesColumnHashSet.add(Contracts.FavoritesEntry._ID);
        favoritesColumnHashSet.add(Contracts.FavoritesEntry.COLUMN_MOVIE_ID);
        favoritesColumnHashSet.add(Contracts.FavoritesEntry.COLUMN_TITLE);
        favoritesColumnHashSet.add(Contracts.FavoritesEntry.COLUMN_POSTER_PATH);
        favoritesColumnHashSet.add(Contracts.FavoritesEntry.COLUMN_OVERVIEW);
        favoritesColumnHashSet.add(Contracts.FavoritesEntry.COLUMN_VOTE_AVERAGE);
        favoritesColumnHashSet.add(Contracts.FavoritesEntry.COLUMN_RELEASE_DATE);

        do {
            String columnName = c.getString(columnNameIndex);
            favoritesColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required trailer
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required trailers entry columns",
                trailersColumnHashSet.isEmpty());
        // if this fails, it means that your database doesn't contain all of the required reviews
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required reviews entry columns",
                reviewsColumnHashSet.isEmpty());
        // if this fails, it means that your database doesn't contain all of the required favorites
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required favorites entry columns",
                favoritesColumnHashSet.isEmpty());

        c.close();
        db.close();
    }

    public void testTables() {
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        insertValues(db, Contracts.TrailersEntry.TABLE_NAME, TestUtilities.createTrailerValues());
        insertValues(db, Contracts.ReviewsEntry.TABLE_NAME, TestUtilities.createReviewsValues());
        insertValues(db, Contracts.FavoritesEntry.TABLE_NAME, TestUtilities.createFavoritesValues());

        db.close();
    }

    private long insertValues(SQLiteDatabase db, String tableName, ContentValues testValues){

        //Insert ContentValues into database and get a row ID back
        long rowId;
        rowId = db.insert(tableName, null, testValues);

        // Verify we got a row back.
        assertTrue(rowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                tableName,  // Table to Query
                null, // All columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // Columns to group by
                null, // Columns to filter by row groups
                null  // Sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from " + tableName + " table query", cursor.moveToFirst() );

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: " + tableName + " Query Validation Failed",
                cursor, testValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse("Error: More than one record returned from " + tableName + " query",
                cursor.moveToNext());

        // Sixth Step: Close Cursor and Database
        cursor.close();
        return rowId;
    }
}
