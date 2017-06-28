package com.example.ahsan.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.ahsan.popularmovies.R;
import com.example.ahsan.popularmovies.Utilities.MoviePreferences;
import com.example.ahsan.popularmovies.data.MovieContract;
import com.example.ahsan.popularmovies.enums.MovieResponse;
import com.example.ahsan.popularmovies.fragments.MovieListing;
import com.squareup.picasso.Picasso;

import static com.example.ahsan.popularmovies.data.MovieContract.MovieBase.COLUMN_MOVIEID;
import static com.orhanobut.logger.Logger.d;

/**
 * Created by Ahsan on 2017-02-07.
 */

public class RAdapter extends RecyclerView.Adapter<RAdapter.ViewHolder> {
    
    private final Context ctx;
    MovieListing callingFragment;
    String image_base_url;
    String imagePath;
    Cursor currentCursor;
    
    
    public RAdapter(Context context, MovieListing movieListing) {
        callingFragment = movieListing;
        ctx = context.getApplicationContext();
        d("loadFlow: RAdapter");
    
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
        int imageColumnIndex = currentCursor.getColumnIndex(MovieContract.MovieBase.COLUMN_POSTERPATH);
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
            currentCursor.moveToPosition(getAdapterPosition());
            String movieId = currentCursor.getString(currentCursor.getColumnIndex(COLUMN_MOVIEID));
            String rating = currentCursor.getString(currentCursor.getColumnIndex(MovieContract.MovieBase.COLUMN_VOTEAVERAGE));
            String releasedate = currentCursor.getString(currentCursor.getColumnIndex(MovieContract.MovieBase.COLUMN_RELEASEDATE));
            String posterpath = currentCursor.getString(currentCursor.getColumnIndex(MovieContract.MovieBase.COLUMN_POSTERPATH));
            String overview = currentCursor.getString(currentCursor.getColumnIndex(MovieContract.MovieBase.COLUMN_OVERVIEW));
            String originaltitle = currentCursor.getString(currentCursor.getColumnIndex(MovieContract.MovieBase.COLUMN_ORIGINALTITLE));
            String favorite = currentCursor.getString(currentCursor.getColumnIndex(MovieContract.MovieBase.COLUMN_FAVORITES));
//            String durationTime = currentCursor.getString(currentCursor.getColumnIndex(MovieContract.Movie.COLUMN_DURATION));
            
            Bundle b = new Bundle();
            
            b.putString(MovieResponse.MOVIEID.name(), movieId);
            
            b.putString(MovieResponse.RATING.name(), rating);
            
            b.putString(MovieResponse.RELEASE_DATE.name(), releasedate);
            
            b.putString(MovieResponse.POSTERPATH.name(), posterpath);
            
            b.putString(MovieResponse.OVERVIEW.name(), overview);
            
            b.putString(MovieResponse.TITLE.name(), originaltitle);
            b.putString(MovieResponse.DURATION.name(), originaltitle);
            
            if(MoviePreferences.isInFavorites(ctx,movieId))
                b.putBoolean(MovieResponse.FAVORITE.name(), true);
            else
                b.putBoolean(MovieResponse.FAVORITE.name(), false);
            
            callingFragment.onButtonPressed(b);
            
        }
        
        
    }
    
    
}
