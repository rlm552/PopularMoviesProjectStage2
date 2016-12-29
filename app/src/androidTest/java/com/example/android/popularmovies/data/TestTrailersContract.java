package com.example.android.popularmovies.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Rory on 12/10/2016.
 */
public class TestTrailersContract extends AndroidTestCase {

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
}
