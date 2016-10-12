package com.example.android.popularmovies;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle extras = getActivity().getIntent().getExtras();
        final String title = extras.getString("Title");
        String posterPath = extras.getString("Poster_Path");
        String overview = extras.getString("Overview");
        String voteAverage = extras.getString("Vote_Average");
        String releaseDate = extras.getString("Release_Date").substring(0, 4);

        final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        TextView textView = (TextView) rootView.findViewById(R.id.movie_title);
        textView.setText(title);

        ImageView imageView = (ImageView) rootView.findViewById(R.id.movie_poster);
        Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w185/" + posterPath).into(imageView);

        textView = (TextView) rootView.findViewById(R.id.movie_overview);
        textView.setText(overview);

        textView = (TextView) rootView.findViewById(R.id.movie_vote_average);
        textView.setText("Vote Average: " + voteAverage);

        textView = (TextView) rootView.findViewById(R.id.movie_release_date);
        textView.setText(releaseDate);



        return rootView;
    }
}
