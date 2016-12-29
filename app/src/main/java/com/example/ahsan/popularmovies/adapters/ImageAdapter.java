package com.example.ahsan.popularmovies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.ahsan.popularmovies.R;
import com.example.ahsan.popularmovies.activities.MainActivity;
import com.example.ahsan.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by ahsan on 2016-12-25.
 */
public class ImageAdapter extends BaseAdapter {
    private final Context ctx;
    ArrayList<Movie> movies;
    MainActivity main;
    int width=0;
    int height=0;
    public ImageAdapter(MainActivity mainActivity,  ArrayList<Movie> items) {
        main = mainActivity;
          ctx =  mainActivity.getApplicationContext();
        movies = items;

    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Movie getItem(int position) {

        return movies.get( position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null){
           convertView = LayoutInflater.from(ctx).
                   inflate(R.layout.layout_list_view_row_items, parent, false);
       }
        final ImageView background = (ImageView) convertView.findViewById(R.id.movie_background);

     //   movieTitle.setText(movies.get(position).getTitle());
        final String ImageURL = movies.get(position).getImageBackground();
        background.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                   width = background.getMeasuredWidth();
                  height = background.getMeasuredHeight();
                Picasso.with(ctx).load(main.getImageBaseURL()+ImageURL) .into(background);
                background.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
        return convertView;
    }
}
