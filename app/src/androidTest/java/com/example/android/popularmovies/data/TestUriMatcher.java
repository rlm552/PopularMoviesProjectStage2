package com.example.android.popularmovies.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Rory on 12/9/2016.
 */
public class TestUriMatcher extends AndroidTestCase {

    // content://com.example.android.popularmovies/trailers/"
    private static final Uri TEST_TRAILERS_DIR = Contracts.TrailersEntry.CONTENT_URI;

    // content://com.example.android.popularmovies/reviews/*
    private static final Uri TEST_REVIEWS_DIR = Contracts.ReviewsEntry.CONTENT_URI;

    /*
        This function tests that your UriMatcher returns the correct integer value
        for each of the Uri types that our ContentProvider can handle.
     */

    public void testUriMatcher() {
        UriMatcher testMatcher = Provider.buildUriMatcher();

        assertEquals("Error: The Trailers URI was matched incorrectly.",
                testMatcher.match(TEST_TRAILERS_DIR), Provider.TRAILERS);

        assertEquals("Error: The Reviews URI was matched incorrectly.",
                testMatcher.match(TEST_REVIEWS_DIR), Provider.REVIEWS);
    }

}
