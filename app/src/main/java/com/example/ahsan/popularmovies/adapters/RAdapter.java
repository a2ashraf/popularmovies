package com.example.ahsan.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.ahsan.popularmovies.R;
import com.example.ahsan.popularmovies.Utilities.MoviePreferences;
import com.example.ahsan.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by Ahsan on 2017-02-07.
 */

public class RAdapter extends RecyclerView.Adapter<RAdapter.ViewHolder> {
    
    private final Context ctx;
    FragmentActivity callingFragment;
    String image_base_url;
    String imagePath;
    Cursor currentCursor;
 
    
    public RAdapter(  Context context) {
      //  callingFragment = movieListing;
        ctx = context;
        
    }
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View movieItem = LayoutInflater.from(ctx).inflate(R.layout.layout_list_view_row_items, parent, false);
        ViewHolder viewHolder = new ViewHolder(movieItem);
        
        return viewHolder;
    }
    
    
    //TODO: why are both tables showing same data.  look at request and compare responses.look below for only setting one thing?
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        currentCursor.moveToPosition(position);
        int imageColumnIndex = currentCursor.getColumnIndex(MovieContract.Movie.COLUMN_POSTERPATH);
        final String ImageURL = currentCursor.getString(imageColumnIndex);
        Picasso.with(ctx).load(MoviePreferences.getBaseUrl(ctx) + "/" + MoviePreferences.getBackDropSize(ctx) + ImageURL).into(holder.background);
        
    }
    
    @Override
    public int getItemCount() {
        
        
        if (null == currentCursor)
            return 0;
        
        return currentCursor.getCount();
    }
    
    public void swapCursor(Cursor data) {
        currentCursor = data;
        notifyDataSetChanged();
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
//            Result aMovie = currentCursor.get(this.getLayoutPosition());
//            Bundle b = new Bundle();
//            b.putString(MovieResponse.TITLE.name(), aMovie.title);
//            b.putString(MovieResponse.OVERVIEW.name(), aMovie.overview);
//            b.putString(MovieResponse.RATING.name(), aMovie.voteAverage + "");
//            b.putString(MovieResponse.RELEASE_DATE.name(), aMovie.releaseDate);
//            ((MovieListing) callingFragment).onButtonPressed(b);
            
        }
    }
    
    
}
