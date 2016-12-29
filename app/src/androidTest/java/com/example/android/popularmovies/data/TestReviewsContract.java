package com.example.android.popularmovies.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Rory on 12/26/2016.
 */
public class TestReviewsContract extends AndroidTestCase{

    public void testBuildReviews(){
        Uri reviewsUri = Contracts.ReviewsEntry.buildReviewsUri(0);

        assertNotNull("Error: Null Uri returned.  You must fill-in buildReviewssUri in " +
                "ReviewsContract.", reviewsUri);

        assertEquals("Error: id not properly appended to the end of the Uri",
                "0", reviewsUri.getLastPathSegment());

        assertEquals("Error: Reviews Uri doesn't match our expected result",
                reviewsUri.toString(),
                "content://com.example.android.popularmovies/reviews/0");
    }
}
