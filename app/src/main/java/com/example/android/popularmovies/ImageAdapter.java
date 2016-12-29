package com.example.android.popularmovies;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Rory on 9/14/2016.
 */
public class ImageAdapter extends BaseAdapter
{
    private final String LOG_TAG = ImageAdapter.class.getSimpleName();

    private Context context;
    private List <Movie> movieObjects;

    public ImageAdapter(Context c, List <Movie> m)
    {
        context = c;
        movieObjects = m;
    }

    //---returns the number of images---
    public int getCount() {
        return movieObjects.size();
    }

    //---returns the ID of an item---
    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    //---returns an ImageView view---
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ImageView imageView;

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;

        if (convertView == null) {
            imageView = new ImageView(context);
            //Ratio of poster height to poster width is 40/27
            imageView.setLayoutParams(new GridView.LayoutParams(screenWidth/2, (screenWidth/2)*40/27));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 0, 0, 0);
        } else {
            imageView = (ImageView) convertView;
        }

        // Only loads images if object exists
        if(movieObjects.get(position) != null) {
            Picasso.with(context).load("http://image.tmdb.org/t/p/w185/" + movieObjects.get(position).posterPath).into(imageView);
        }
        return imageView;
    }
}
