package com.example.ahsan.popularmovies.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.example.ahsan.popularmovies.data.MovieContract;
import com.example.ahsan.popularmovies.model.MovieResult;
import com.example.ahsan.popularmovies.model.Movies;
import com.example.ahsan.popularmovies.model.details.MovieDetails;
import com.example.ahsan.popularmovies.webservices.RemoteMoviesAPI;
import com.orhanobut.logger.Logger;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ahsan on 2017-05-29.
 */

public class MovieSyncTask {
    //TODO: FIX this so that you send in from intent service the id.
    public static final int TOP_RATED = 11;
    public static final int POPULAR = 12;
    public static final int FAVORITES = 13;
    
    synchronized public static void syncMovies(final Context context,  int action, int movieid) {
        //make the network calls!, sync the response with the database insert queries.
        final ContentResolver contentResolver = context.getContentResolver();
    
        switch (action) {
            case (MovieUtils.ACTION_LOOKUP_MOVIES):
                RemoteMoviesAPI.getInstance().getTopRated(null).enqueue(new Callback<Movies>() {
                    
                    
    
                    @Override
                    public void onResponse(Call<Movies> call, Response<Movies> response) {
                        Logger.d(" ");
                        ContentValues[] freshListOfMovies = getContentValuesFromMovieLookup(response,TOP_RATED);
                         
                        if (freshListOfMovies.length != 0 && freshListOfMovies != null) {
                             contentResolver.bulkInsert(MovieContract.MovieBase.CONTENT_URI, freshListOfMovies);
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<Movies> call, Throwable t) {
                        Logger.d(t.getMessage());
                        new Throwable(t);
                    }
                });
                
                
                RemoteMoviesAPI.getInstance().getPopular(null).enqueue(new Callback<Movies>() {
                    @Override
                    public void onResponse(Call<Movies> call, Response<Movies> response) {
                        Logger.d(response.body().toString());
                        ContentValues[] freshListOfMovies = getContentValuesFromMovieLookup(response, POPULAR);
                        if (freshListOfMovies.length != 0 && freshListOfMovies != null) {
                             contentResolver.bulkInsert(MovieContract.MovieBase.CONTENT_URI, freshListOfMovies);
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<Movies> call, Throwable t) {
                        Logger.d(t.getMessage());
                        new Throwable(t);
                    }
                });
                break;

            default:
                throw new UnsupportedOperationException("Uknown action: " + action);
        }
    }
    
    private static ContentValues[] getContentValuesFromMovieLookup(Response<Movies> response, int sourceData) {
        List<MovieResult> movieResultList = response.body().movieResults;
        
        ContentValues[] listOfMovies = new ContentValues[movieResultList.size()];
        ContentValues movie;
        for (int index = 0; index < movieResultList.size(); index++) {
            movie = new ContentValues();
            movie.put(MovieContract.MovieBase.COLUMN_MOVIEID, movieResultList.get(index).id);
            movie.put(MovieContract.MovieBase.COLUMN_ORIGINALTITLE, movieResultList.get(index).originalTitle);
            movie.put(MovieContract.MovieBase.COLUMN_OVERVIEW, movieResultList.get(index).overview);
            movie.put(MovieContract.MovieBase.COLUMN_POSTERPATH, movieResultList.get(index).posterPath);
            movie.put(MovieContract.MovieBase.COLUMN_RELEASEDATE, movieResultList.get(index).releaseDate);
            movie.put(MovieContract.MovieBase.COLUMN_VOTEAVERAGE, movieResultList.get(index).voteAverage);
            movie.put(MovieContract.MovieBase.COLUMN_TABLE_NAME,sourceData);
            listOfMovies[index] = movie;
         }
         
        return listOfMovies;
    }
    
    
    
    private static ContentValues getContentValuesFromAMovieLookup(Response<MovieDetails> response) {
        MovieDetails movie = response.body();
    
       ContentValues movieContentValue = new ContentValues();
        movieContentValue.put((MovieContract.MovieBase.COLUMN_MOVIEID), movie.getId());
        movieContentValue.put((MovieContract.Movie.COLUMN_DURATION), movie.getRuntime());

 
        return movieContentValue;
    }
}
