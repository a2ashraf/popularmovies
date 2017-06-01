package com.example.ahsan.popularmovies.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.example.ahsan.popularmovies.data.MovieContract;
import com.example.ahsan.popularmovies.model.Movies;
import com.example.ahsan.popularmovies.model.Result;
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
    
    
    synchronized public static void syncMovies(final Context context) {
        //make the network calls!, sync the response with the database insert queries.
        
        RemoteMoviesAPI.getInstance().getTopRated(null).enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(Call<Movies> call, Response<Movies> response) {
                Logger.d(" ");
                ContentValues[] freshListOfMovies = getContentValuesFromResponse(response);
                //insert into database.
                
                if(freshListOfMovies.length!=0 && freshListOfMovies!=null){
                    ContentResolver moviesContentResolver = context.getContentResolver();
                    //delete first?
                    moviesContentResolver.bulkInsert(MovieContract.MovieTopRated.CONTENT_URI, freshListOfMovies);
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
                ContentValues[] freshListOfMovies = getContentValuesFromResponse(response);
                if(freshListOfMovies.length!=0 && freshListOfMovies!=null){
                    ContentResolver moviesContentResolver = context.getContentResolver();
                    //delete first?
                    moviesContentResolver.bulkInsert(MovieContract.MoviePopular.CONTENT_URI, freshListOfMovies);
                }
            }
            
            @Override
            public void onFailure(Call<Movies> call, Throwable t) {
                Logger.d(t.getMessage());
                new Throwable(t);
            }
        });
        
    }
    
    private static ContentValues[] getContentValuesFromResponse(Response<Movies> response) {
        List<Result> resultList = response.body().results;
        ContentValues[] listOfMovies = new ContentValues[resultList.size()];
        ContentValues movieDetails;
        for (int index = 0; index < resultList.size(); index++) {
             movieDetails = new ContentValues();
            movieDetails.put(MovieContract.Movie.COLUMN_MOVIEID, resultList.get(index).id);
            movieDetails.put(MovieContract.Movie.COLUMN_ORIGINALTITLE, resultList.get(index).originalTitle);
            movieDetails.put(MovieContract.Movie.COLUMN_OVERVIEW, resultList.get(index).overview);
            movieDetails.put(MovieContract.Movie.COLUMN_POSTERPATH, resultList.get(index).posterPath);
            movieDetails.put(MovieContract.Movie.COLUMN_RELEASEDATE, resultList.get(index).releaseDate);
            movieDetails.put(MovieContract.Movie.COLUMN_VOTEAVERAGE, resultList.get(index).voteAverage);
             listOfMovies[index] = movieDetails;
            movieDetails = null;
        }
        return listOfMovies;
    }
    
}
