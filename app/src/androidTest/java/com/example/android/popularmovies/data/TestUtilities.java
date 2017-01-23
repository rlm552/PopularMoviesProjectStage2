package com.example.android.popularmovies.data;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;
import com.example.android.popularmovies.utils.PollingCheck;
import java.util.Map;
import java.util.Set;

public class TestUtilities extends AndroidTestCase{

    private static final int TEST_MOVIE_ID = 32323;
    private static final String TEST_FAVORITE = "Magical Emporium of Professor Blunderbuss";
    private static final String TEST_POSTER_PATH = "f445sffd";
    private static final String TEST_OVERVIEW = "A cool movie";
    private static final String TEST_VOTE_AVERAGE = "6.9";
    private static final String TEST_RELEASE_DATE = "2015";

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createFavoritesValues() {
        // Create a new favorite values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(Contracts.FavoritesEntry.COLUMN_MOVIE_ID, TEST_MOVIE_ID);
        testValues.put(Contracts.FavoritesEntry.COLUMN_TITLE, TEST_FAVORITE);
        testValues.put(Contracts.FavoritesEntry.COLUMN_POSTER_PATH, TEST_POSTER_PATH);
        testValues.put(Contracts.FavoritesEntry.COLUMN_OVERVIEW, TEST_OVERVIEW);
        testValues.put(Contracts.FavoritesEntry.COLUMN_VOTE_AVERAGE, TEST_VOTE_AVERAGE);
        testValues.put(Contracts.FavoritesEntry.COLUMN_RELEASE_DATE, TEST_RELEASE_DATE);

        return testValues;
    }

    static ContentValues createTrailerValues() {
        ContentValues testValues = new ContentValues();
        testValues.put(Contracts.TrailersEntry.COLUMN_MOVIE_ID, 342324);
        testValues.put(Contracts.TrailersEntry.COLUMN_NAME, "The Wizarding World");
        testValues.put(Contracts.TrailersEntry.COLUMN_KEY, "fjlj2ijfd");

        return testValues;
    }

    static ContentValues createReviewsValues() {
        ContentValues testValues = new ContentValues();
        testValues.put(Contracts.ReviewsEntry.COLUMN_MOVIE_ID, 3233233);
        testValues.put(Contracts.ReviewsEntry.COLUMN_REVIEW, "A fun affair for all!");
        testValues.put(Contracts.ReviewsEntry.COLUMN_URL, "https://cool.com");
        testValues.put(Contracts.ReviewsEntry.COLUMN_REVIEW_BUTTON, "Read More");

        return  testValues;
    }

    /*
        Students: The functions we provide inside of TestProvider use this utility class to test
        the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
        CTS tests.

        Note that this only tests that the onChange function is called; it does not test that the
        correct Uri is returned.
     */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }

}
