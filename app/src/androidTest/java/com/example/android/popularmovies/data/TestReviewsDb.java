package com.example.android.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by Rory on 12/23/2016.
 */
public class TestReviewsDb extends AndroidTestCase{

    public static final String LOG_TAG = TestReviewsDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
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
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(Contracts.ReviewsEntry.TABLE_NAME);

        //Favorites Db
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

        // if this fails, it means that your database doesn't have a reviews entry table
        assertTrue("Error: Your database was created without reviews entry table",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + Contracts.ReviewsEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> reviewsColumnHashSet = new HashSet<String>();
        reviewsColumnHashSet.add(Contracts.ReviewsEntry._ID);
        reviewsColumnHashSet.add(Contracts.ReviewsEntry.COLUMN_MOVIE_ID);
        reviewsColumnHashSet.add(Contracts.ReviewsEntry.COLUMN_REVIEW);
        reviewsColumnHashSet.add(Contracts.ReviewsEntry.COLUMN_URL);
        reviewsColumnHashSet.add(Contracts.ReviewsEntry.COLUMN_REVIEW_BUTTON);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            reviewsColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required favorites entry columns",
                reviewsColumnHashSet.isEmpty());
        db.close();
    }
    public void testReviewsTable() {
        insertReviews();
    }

    public long insertReviews(){
        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step: Create ContentValues of what you want to insert
        ContentValues testValues = TestUtilities.createReviewsValues();

        // Third Step: Insert ContentValues into database and get a row ID back
        long reviewsRowId;
        reviewsRowId = db.insert(Contracts.ReviewsEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(reviewsRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                Contracts.ReviewsEntry.TABLE_NAME, //Table to Query
                null, // All columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // Columns to group by
                null, // Columns to filter by row groups
                null  // Sort order
                );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue("Error: No Records returned from reviewsdb query", cursor.moveToFirst());

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Reviews Query Validation Failed",
                cursor, testValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse("Error: More than one record returned from Reviews query",
                cursor.moveToNext());

        // Sixth Step: Close Cursor and Database
        cursor.close();
        db.close();
        return reviewsRowId;
    }

}
