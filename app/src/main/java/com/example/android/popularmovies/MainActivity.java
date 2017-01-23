package com.example.android.popularmovies;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.ViewGroup;
import android.widget.ScrollView;

public class MainActivity extends AppCompatActivity
    implements MainFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getResources().getBoolean(R.bool.twoPaneMode)) {

            if (savedInstanceState == null) {
                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

                Bundle detailsMessage = new Bundle();
                Bundle trailersMessage = new Bundle();
                Bundle reviewsMessage = new Bundle();

                detailsMessage.putString("Message", "Click a poster to display details.");
                trailersMessage.putString("Message", "Click a poster to display movie trailers.");
                reviewsMessage.putString("Message", "Click a poster to display any movie reviews.");

                PlaceHolderFragment detailsPlaceHolder = new PlaceHolderFragment();
                detailsPlaceHolder.setArguments(detailsMessage);
                fragmentTransaction.replace(R.id.two_pane_details_container, detailsPlaceHolder);

                PlaceHolderFragment trailersPlaceHolder = new PlaceHolderFragment();
                trailersPlaceHolder.setArguments(trailersMessage);
                fragmentTransaction.replace(R.id.two_pane_trailers_container, trailersPlaceHolder);

                PlaceHolderFragment reviewsPlaceHolder = new PlaceHolderFragment();
                reviewsPlaceHolder.setArguments(reviewsMessage);
                fragmentTransaction.replace(R.id.two_pane_reviews_container, reviewsPlaceHolder);

                fragmentTransaction.commit();
            }
        }
    }

    public void onFragmentInteraction(Bundle bundle, boolean dataSetChanged){

        if (dataSetChanged){
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            Bundle detailsMessage = new Bundle();
            Bundle trailersMessage = new Bundle();
            Bundle reviewsMessage = new Bundle();

            detailsMessage.putString("Message", "Click a poster to display details.");
            trailersMessage.putString("Message", "Click a poster to display movie trailers.");
            reviewsMessage.putString("Message", "Click a poster to display any movie reviews.");

            PlaceHolderFragment detailsPlaceHolder = new PlaceHolderFragment();
            detailsPlaceHolder.setArguments(detailsMessage);
            fragmentTransaction.replace(R.id.two_pane_details_container, detailsPlaceHolder);

            PlaceHolderFragment trailersPlaceHolder = new PlaceHolderFragment();
            trailersPlaceHolder.setArguments(trailersMessage);
            fragmentTransaction.replace(R.id.two_pane_trailers_container, trailersPlaceHolder);

            PlaceHolderFragment reviewsPlaceHolder = new PlaceHolderFragment();
            reviewsPlaceHolder.setArguments(reviewsMessage);
            fragmentTransaction.replace(R.id.two_pane_reviews_container, reviewsPlaceHolder);

            fragmentTransaction.commit();
        } else {
            DetailFragment detailFragment = new DetailFragment();
            TrailerFragment trailerFragment = new TrailerFragment();
            ReviewFragment reviewFragment = new ReviewFragment();

            //Send data to fragment UI
            detailFragment.setArguments(bundle);
            trailerFragment.setArguments(bundle);
            reviewFragment.setArguments(bundle);

            //update fragments
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            fragmentTransaction.replace(R.id.two_pane_details_container, detailFragment);
            fragmentTransaction.replace(R.id.two_pane_trailers_container, trailerFragment);
            fragmentTransaction.replace(R.id.two_pane_reviews_container, reviewFragment);

            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onStart(){
        super.onStart();
        if (getResources().getBoolean(R.bool.twoPaneMode)){
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            int screenWidth = metrics.widthPixels;
            android.support.v4.app.Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment);
            ViewGroup.LayoutParams params = fragment.getView().getLayoutParams();
            params.width = screenWidth/2;
            fragment.getView().setLayoutParams(params);
        }
    }
}
