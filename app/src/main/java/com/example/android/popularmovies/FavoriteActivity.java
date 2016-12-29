package com.example.android.popularmovies;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import com.example.android.popularmovies.data.FavoritesContract;
import com.example.android.popularmovies.data.FavoritesDBHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        RecyclerView mRecyclerView;
        String[] mDataSet;

        setContentView(R.layout.favorite_detail);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mDataSet = returnFavorites();
        mAdapter = new RecycleAdapter(mDataSet);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private String[] returnFavorites(){
        String[] myDataSet = new String[0];
        List<String> myFavoriteList = new ArrayList<>(Arrays.asList(myDataSet));

        int columnTitle = 1;

        SQLiteDatabase db = new FavoritesDBHelper(
                this).getWritableDatabase();

        Cursor cursor = db.query(
                FavoritesContract.FavoritesEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Go to the first row
        cursor.moveToFirst();
        String favorite;

        // Iterate through every row, compare favorite in database to title in view
        for (int i = 0; i < cursor.getCount(); i++) {
            favorite = cursor.getString(columnTitle);
            myFavoriteList.add(favorite);

            //Move to next row
            cursor.moveToNext();
        }

        cursor.close();
        db.close();

        myDataSet = myFavoriteList.toArray(new String[0]);
        return myDataSet;
    }
}
