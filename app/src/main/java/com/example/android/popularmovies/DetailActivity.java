package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ScrollView;


public class DetailActivity extends AppCompatActivity {

    static final String LOG_TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ScrollView scrollView = (ScrollView) findViewById(R.id.detailScroll);
        scrollView.smoothScrollTo(0, 0);

        if (savedInstanceState == null) {
                        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            DetailFragment detailFragment = new DetailFragment();
            fragmentTransaction.replace(R.id.details_container, detailFragment);

            TrailerFragment trailerFragment = new TrailerFragment();
            fragmentTransaction.replace(R.id.trailers_container, trailerFragment);

            ReviewFragment reviewFragment = new ReviewFragment();
            fragmentTransaction.replace(R.id.reviews_container, reviewFragment);

            fragmentTransaction.commit();
        }
    }
}

