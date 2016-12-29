package com.example.android.popularmovies;

/**
 * Created by Rory on 9/15/2016.
 */
public class Movie {
    int movieID;
    String title;
    String posterPath;
    String overview;
    String voteAverage;
    String releaseDate;

    public Movie(int movieID, String title, String posterPath, String overview, String voteAverage, String releaseDate){
        this.movieID = movieID;
        this.title = title;
        this.posterPath = posterPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }
}
