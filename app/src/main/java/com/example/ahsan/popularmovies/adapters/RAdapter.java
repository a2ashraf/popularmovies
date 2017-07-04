package com.example.ahsan.popularmovies.adapters;

import android.content.Context;
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
import com.example.ahsan.popularmovies.model.details.MovieDetails;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.orhanobut.logger.Logger.d;

/**
 * Created by Ahsan on 2017-02-07.
 */

public class RAdapter extends RecyclerView.Adapter<RAdapter.ViewHolder> {
    
    private final Context ctx;
    MovieListing callingFragment;
    ArrayList<AMovieOFDetails> movies;
    
    
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
    
    
    
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (movies != null) {
            MovieDetails aMovie = movies.get(position);
            String ImageURL = aMovie.getPosterPath();
            Picasso.with(ctx).load(MoviePreferences.getBaseUrl(ctx) + "/" + MoviePreferences.getBackDropSize(ctx) + ImageURL).into(holder.background);
        }
        
    }
    
    @Override
    public int getItemCount() {
        if (null == movies)
            return 0;
        return movies.size();
    }
    public void swapCursor(ArrayList<AMovieOFDetails>  allMovies) {
        movies=allMovies;
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
            AMovieOFDetails aMovieOFDetails = movies.get(getAdapterPosition());
            String movieId = String.valueOf(aMovieOFDetails.getId());
            String rating = String.valueOf(aMovieOFDetails.getVoteAverage());
            String releasedate = String.valueOf(aMovieOFDetails.getReleaseDate());
            String posterpath = String.valueOf(aMovieOFDetails.getPosterPath());
            String overview = String.valueOf(aMovieOFDetails.getOverview());
            String originaltitle = String.valueOf(aMovieOFDetails.getOriginalTitle());
            String durationTime = String.valueOf(aMovieOFDetails.getRuntime());

            int favorite = aMovieOFDetails.getFavorite();
            int movieType = aMovieOFDetails.getMovieType();

             
            Bundle b = new Bundle();
            b.putInt(MovieContract.MovieBase.COLUMN_TABLE_NAME, movieType);
            b.putString(MovieResponse.MOVIEID.name(), movieId);
            
            b.putString(MovieResponse.RATING.name(), rating);
            
            b.putString(MovieResponse.RELEASE_DATE.name(), releasedate);
            
            b.putString(MovieResponse.POSTERPATH.name(), posterpath);
            
            b.putString(MovieResponse.OVERVIEW.name(), overview);
            
            b.putString(MovieResponse.TITLE.name(), originaltitle);
             b.putInt(MovieResponse.FAVORITE.name(), favorite);
            callingFragment.onButtonPressed(b);
            
        }
        
        
    }
    
    public static class AMovieOFDetails extends MovieDetails {
        int favorite = -1;
        int movieType = -1;
        
        public int getFavorite() {
            return favorite;
        }
        
        public void setFavorite(int favorite) {
            this.favorite = favorite;
        }
        
        public int getMovieType() {
            return movieType;
        }
        
        public void setMovieType(int movieType) {
            this.movieType = movieType;
        }
    }
    
    
}
