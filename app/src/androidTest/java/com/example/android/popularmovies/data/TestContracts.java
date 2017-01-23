package com.example.android.popularmovies.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Rory on 12/26/2016.
 */
public class TestContracts extends AndroidTestCase{

    public void testBuildTrailer(){
        Uri trailersUri = Contracts.TrailersEntry.buildTrailersUri(0);

        assertNotNull("Error: Null Uri returned.  You must fill-in buildTrailersUri in " +
                        "TrailersContract.",
                trailersUri);

        assertEquals("Error: id not properly appended to the end of the Uri",
                "0", trailersUri.getLastPathSegment());

        assertEquals("Error: Trailers Uri doesn't match our expected result",
                trailersUri.toString(),
                "content://com.example.android.popularmovies/trailers/0");
    }

    public void testBuildReviews(){
        Uri reviewsUri = Contracts.ReviewsEntry.buildReviewsUri(0);

        assertNotNull("Error: Null Uri returned.  You must fill-in buildReviewsUri in " +
                "ReviewsContract.", reviewsUri);

        assertEquals("Error: id not properly appended to the end of the Uri",
                "0", reviewsUri.getLastPathSegment());

        assertEquals("Error: Reviews Uri doesn't match our expected result",
                reviewsUri.toString(),
                "content://com.example.android.popularmovies/reviews/0");
    }

    public void testBuildFavorites(){
        Uri favoritesUri = Contracts.FavoritesEntry.buildFavoritesUri(0);

        assertNotNull("Error: Null Uri returned.  You must fill-in buildFavoritesUri in " +
                "FavoritesContract.", favoritesUri);

        assertEquals("Error: id not properly appended to the end of the Uri",
                "0", favoritesUri.getLastPathSegment());

        assertEquals("Error: Favorites Uri doesn't match our expected result",
                favoritesUri.toString(),
                "content://com.example.android.popularmovies/favorites/0");
    }

}
