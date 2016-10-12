package com.example.android.popularmovies;

/**
 * Created by Rory on 9/15/2016.
 */
public class Movie {
    String title;
    String posterPath;
    String overview;
    String voteAverage;
    String releaseDate;

    public Movie(String title, String posterPath, String overview, String voteAverage, String releaseDate){
        this.title = title;
        this.posterPath = posterPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }
}
