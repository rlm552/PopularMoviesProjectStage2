package com.example.android.popularmovies;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.TextView;

/**
 * Created by Rory on 1/16/2017.
 */
public class PlaceHolderFragment extends Fragment{


    public PlaceHolderFragment(){

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_placeholder, container, false);
        Bundle bundle = getArguments();
        if (bundle != null){
            String message = bundle.getString("Message");
            TextView textView = (TextView) rootView.findViewById(R.id.placeholder);
            textView.setText(message);
        }
        return rootView;
    }

}
