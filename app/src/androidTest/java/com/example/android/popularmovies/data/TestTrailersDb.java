package com.example.android.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by Rory on 12/9/2016.
 */
public class TestTrailersDb extends AndroidTestCase {
    public static final String LOG_TAG = TestTrailersDb.class.getSimpleName();

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
        tableNameHashSet.add(Contracts.TrailersEntry.TABLE_NAME);

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

        // if this fails, it means that your database doesn't have a trailers entry table
        assertTrue("Error: Your database was created without trailers entry table",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + Contracts.TrailersEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> trailersColumnHashSet = new HashSet<String>();
        trailersColumnHashSet.add(Contracts.TrailersEntry._ID);
        trailersColumnHashSet.add(Contracts.TrailersEntry.COLUMN_MOVIE_ID);
        trailersColumnHashSet.add(Contracts.TrailersEntry.COLUMN_NAME);
        trailersColumnHashSet.add(Contracts.TrailersEntry.COLUMN_KEY);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            trailersColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required favorites entry columns",
                trailersColumnHashSet.isEmpty());
        db.close();
    }

    /*
        Students:  Here is where you will build code to test that we can insert and query the
        location database.  You'll want to look in TestUtilities
        where you can uncomment out the "createNorthPoleLocationValues" function.  You can
        also make use of the ValidateCurrentRecord function from within TestUtilities.
    */
    public void testTrailersTable() {
        insertTrailers();
    }



    public long insertTrailers(){
        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step: Create ContentValues of what you want to insert
        ContentValues testValues = TestUtilities.createTrailerValues();

        // Third Step: Insert ContentValues into database and get a row ID back
        long trailersRowId;
        trailersRowId = db.insert(Contracts.TrailersEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(trailersRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                Contracts.TrailersEntry.TABLE_NAME,  // Table to Query
                null, // All columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // Columns to group by
                null, // Columns to filter by row groups
                null  // Sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from trailersdb query", cursor.moveToFirst() );

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Trailers Query Validation Failed",
                cursor, testValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from Trailers query",
                cursor.moveToNext() );

        // Sixth Step: Close Cursor and Database
        cursor.close();
        db.close();
        return trailersRowId;
    }
}
