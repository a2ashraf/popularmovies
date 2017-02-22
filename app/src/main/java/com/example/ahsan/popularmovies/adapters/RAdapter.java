package com.example.ahsan.popularmovies.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.ahsan.popularmovies.R;
import com.example.ahsan.popularmovies.enums.MovieResponse;
import com.example.ahsan.popularmovies.fragments.MovieListing;
import com.example.ahsan.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Ahsan on 2017-02-07.
 */

public class RAdapter extends RecyclerView.Adapter<RAdapter.ViewHolder> {

    private final Context ctx;
    ArrayList<Movie> movies;
    Fragment callingFragment;


    public RAdapter(Fragment fragment, ArrayList<Movie> items) {
        callingFragment = fragment;
        ctx = fragment.getActivity();
        movies = items;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View movieItem = LayoutInflater.from(ctx).inflate(R.layout.layout_list_view_row_items, parent, false);
        ViewHolder viewHolder = new ViewHolder(movieItem);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final String ImageURL = movies.get(position).getImageBackground();
        Picasso.with(ctx).load(((MovieListing) callingFragment).getImageBaseURL() + ImageURL).into(holder.background);

    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView background;

        public ViewHolder(View itemView) {
            super(itemView);

            if (itemView != null) {
                background =
                        (ImageView) itemView.findViewById(R.id.movie_imageview);
                background.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            Movie aMovie = movies.get((int) this.getLayoutPosition());
            Bundle b = new Bundle();
            b.putString(MovieResponse.TITLE.name(), aMovie.getTitle());
            b.putString(MovieResponse.OVERVIEW.name(), aMovie.getOverview());
            b.putString(MovieResponse.RATING.name(), aMovie.getUserRating() + "");
            b.putString(MovieResponse.RELEASE_DATE.name(), aMovie.getReleaseDate());
            b.putString(MovieResponse.THUMBNAIL.name(), ((MovieListing) callingFragment).getImageBaseURL() + aMovie.getImageBackground());
            ((MovieListing) callingFragment).onButtonPressed(b);


        }
    }


}
