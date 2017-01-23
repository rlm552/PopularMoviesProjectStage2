package com.example.android.popularmovies.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

/**
 * Created by Rory on 12/31/2016.
 */
public class TestFavoritesProvider extends AndroidTestCase {
    private static final String LOG_TAG = TestFavoritesProvider.class.getSimpleName();

    /*
       This helper function deletes all records from both database tables using the ContentProvider.
       It also queries the ContentProvider to make sure that the database has been successfully
       deleted, so it cannot be used until the Query and Delete functions have been written
       in the ContentProvider.
       Students: Replace the calls to deleteAllRecordsFromDB with this one after you have written
       the delete functionality in the ContentProvider.
     */

    private void deleteAllRecordsFromProvider(){
        int rowsDeleted = mContext.getContentResolver().delete(
                Contracts.FavoritesEntry.CONTENT_URI,
                null,
                null
        );
        Log.v(LOG_TAG, "rows deleted: " + rowsDeleted);

        Cursor cursor = mContext.getContentResolver().query(
                Contracts.FavoritesEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals("Error: Records not deleted from Favorites table during delete", 0, cursor.getCount());
        cursor.close();
    }

    private void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    /*
        This test checks to make sure that the content provider is registered correctly.
     */

    public  void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // ReviewsProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                Provider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: FavoritesProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + Contracts.CONTENT_AUTHORITY,
                    providerInfo.authority, Contracts.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: FavoritesProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    /*
    This test doesn't touch the database.  It verifies that the ContentProvider returns
    the correct type for each type of URI that it can handle.
    */
    public void testGetType(){
        // content://com.example.android.popularmovies/favorites
        String type = mContext.getContentResolver().getType(Contracts.FavoritesEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.popularmovies/favorites
        assertEquals("Error: the Favorites Entry CONTENT_URI should return FavoritesEntry.CONTENT_TYPE",
                Contracts.FavoritesEntry.CONTENT_TYPE, type);
    }

    /*
        This test uses the database directly to insert and then uses the ContentProvider to
        read out the data.
     */
    public void testBasicFavoritesQuery() {
        // insert our test records into the database
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createFavoritesValues();
        long favoritesRowId = db.insert(Contracts.FavoritesEntry.TABLE_NAME, null, testValues);
        assertTrue("Unable to Insert FavoritesEntry into the Database", favoritesRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor favoritesCursor = mContext.getContentResolver().query(
                Contracts.FavoritesEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicReviewsQuery", favoritesCursor, testValues);
    }

    public void testUpdateFavorites() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createFavoritesValues();

        Uri favoritesUri = mContext.getContentResolver().
                insert(Contracts.FavoritesEntry.CONTENT_URI, values);
        long favoritesRowId = ContentUris.parseId(favoritesUri);

        // Verify we got a row back.
        assertTrue(favoritesRowId != -1);
        Log.d(LOG_TAG, "New row id: " + favoritesRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(Contracts.FavoritesEntry._ID, favoritesRowId);
        updatedValues.put(Contracts.FavoritesEntry.COLUMN_MOVIE_ID, 21223);
        updatedValues.put(Contracts.FavoritesEntry.COLUMN_TITLE, "Ferris Bueller");
        updatedValues.put(Contracts.FavoritesEntry.COLUMN_POSTER_PATH, "https://poster.com");
        updatedValues.put(Contracts.FavoritesEntry.COLUMN_VOTE_AVERAGE, "6.9");
        updatedValues.put(Contracts.FavoritesEntry.COLUMN_RELEASE_DATE, "12/15");

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor favoritesCursor = mContext.getContentResolver().query(Contracts.FavoritesEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        favoritesCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                Contracts.FavoritesEntry.CONTENT_URI, updatedValues, Contracts.FavoritesEntry._ID + "= ?",
                new String[] { Long.toString(favoritesRowId)});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        // If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        favoritesCursor.unregisterContentObserver(tco);
        favoritesCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                Contracts.FavoritesEntry.CONTENT_URI,
                null,   // projection
                Contracts.FavoritesEntry._ID + " = " + favoritesRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateFavorites.  Error validating favorites entry update.",
                cursor, updatedValues);

        deleteAllRecords();

        cursor.close();
    }

    private void testInsertReadProvider() {
        ContentValues testValues = TestUtilities.createFavoritesValues();

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(Contracts.FavoritesEntry.CONTENT_URI, true, tco);
        Uri favoritesUri = mContext.getContentResolver().insert(Contracts.FavoritesEntry.CONTENT_URI, testValues);

        // Did our content observer get called? If this fails, your insert location
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long favoritesRowId = ContentUris.parseId(favoritesUri);

        // Verify we got a row back.
        assertTrue(favoritesRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                Contracts.FavoritesEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating FavoritesEntry.",
                cursor, testValues);

    }

    public void testDeleteRecords() {
        testInsertReadProvider();

        // Register a content observer for our reviews delete.
        TestUtilities.TestContentObserver reviewsObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(Contracts.FavoritesEntry.CONTENT_URI, true, reviewsObserver);

        deleteAllRecordsFromProvider();

        // If either of these fail, you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in the ContentProvider
        // delete.  (only if the insertReadProvider is succeeding)
        reviewsObserver.waitForNotificationOrFail();


        mContext.getContentResolver().unregisterContentObserver(reviewsObserver);

    }

}
