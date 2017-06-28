package com.example.ahsan.popularmovies.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.example.ahsan.popularmovies.data.MovieContract;
import com.example.ahsan.popularmovies.model.AMovie;
import com.example.ahsan.popularmovies.model.MovieResult;
import com.example.ahsan.popularmovies.model.Movies;
import com.example.ahsan.popularmovies.model.ReviewResult;
import com.example.ahsan.popularmovies.model.Reviews;
import com.example.ahsan.popularmovies.model.TrailerResult;
import com.example.ahsan.popularmovies.model.Trailers;
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
    
    synchronized public static void syncMovies(final Context context,  int action, int movieid) {
        //make the network calls!, sync the response with the database insert queries.
        final ContentResolver contentResolver = context.getContentResolver();
    
        switch (action) {
            case (MovieUtils.ACTION_LOOKUP_MOVIES):
                RemoteMoviesAPI.getInstance().getTopRated(null).enqueue(new Callback<Movies>() {
                    @Override
                    public void onResponse(Call<Movies> call, Response<Movies> response) {
                        Logger.d(" ");
                        ContentValues[] freshListOfMovies = getContentValuesFromMovieLookup(response);
                        //insert into database.
                        
                        if (freshListOfMovies.length != 0 && freshListOfMovies != null) {
                             //delete first?
                            contentResolver.bulkInsert(MovieContract.MovieTopRated.CONTENT_URI, freshListOfMovies);
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
                        ContentValues[] freshListOfMovies = getContentValuesFromMovieLookup(response);
                        if (freshListOfMovies.length != 0 && freshListOfMovies != null) {
                            //delete first?
                            contentResolver.bulkInsert(MovieContract.MoviePopular.CONTENT_URI, freshListOfMovies);
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<Movies> call, Throwable t) {
                        Logger.d(t.getMessage());
                        new Throwable(t);
                    }
                });
                break;
            case (MovieUtils.ACTION_LOOKUP_REVIEWS):
       
                RemoteMoviesAPI.getInstance().getReviews(String.valueOf(movieid), null).enqueue(new Callback<Reviews>() {
                    @Override
                    public void onResponse(Call<Reviews> call, Response<Reviews> response) {
                        Logger.d(response.body().toString());
                        ContentValues[] freshListOfMovies = getContentValuesFromMovieReviewLookup(response);
                        if (freshListOfMovies.length != 0 && freshListOfMovies != null) {
                            //delete first?
                            contentResolver.bulkInsert(MovieContract.MovieReview.CONTENT_URI, freshListOfMovies);
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<Reviews> call, Throwable t) {
                        Logger.d(t.getMessage());
                        new Throwable(t);
                    }
                });
                
                
                break;
            case (MovieUtils.ACTION_LOOKUP_MOVIE):
                RemoteMoviesAPI.getInstance().getMovie(String.valueOf(movieid), null).enqueue(new Callback<AMovie>() {
                    @Override
                    public void onResponse(Call<AMovie> call, Response<AMovie> response) {
                    
                      if(response!=null && response.body()!=null){
                          ContentValues movie  = getContentValuesFromAMovieLookup(response);
                          contentResolver.insert(MovieContract.Movie.CONTENT_URI, movie);
                      }
                    }
    
                    @Override
                    public void onFailure(Call<AMovie> call, Throwable t) {
                        Logger.d(t.getMessage());
                        new Throwable(t);
                    }
    
                });
                break;
            case (MovieUtils.ACTION_LOOKUP_TRAILERS):
                RemoteMoviesAPI.getInstance().getVideos(String.valueOf(movieid), null).enqueue(new Callback<Trailers>() {
                    @Override
                    public void onResponse(Call<Trailers> call, Response<Trailers> response) {
                         ContentValues[] freshListOfMovies = getContentValuesFromTrailerLookup(response);
                        if (freshListOfMovies.length != 0 && freshListOfMovies != null) {
                            //delete first?
                            contentResolver.bulkInsert(MovieContract.MovieTrailers.CONTENT_URI, freshListOfMovies);
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<Trailers> call, Throwable t) {
                        Logger.d(t.getMessage());
                        new Throwable(t);
                    }
                });
                break;
            default:
                throw new UnsupportedOperationException("Uknown action: " + action);
        }
    }
    
    private static ContentValues[] getContentValuesFromMovieLookup(Response<Movies> response) {
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
            listOfMovies[index] = movie;
         }
         
        return listOfMovies;
    }
    
    private static ContentValues[] getContentValuesFromTrailerLookup(Response<Trailers> response) {
        
        List<TrailerResult> trailerResultList = response.body().results;
        ContentValues[] listOfTrailers = new ContentValues[trailerResultList.size()];
        ContentValues movieTrailers;
        for (int index = 0; index < trailerResultList.size(); index++) {
            movieTrailers = new ContentValues();
            movieTrailers.put(MovieContract.MovieTrailers.COLUMN_MOVIEID, response.body().id);
            movieTrailers.put(MovieContract.MovieTrailers.COLUMN_TRAILER_KEY, trailerResultList.get(index).key);
            listOfTrailers[index] = movieTrailers;
        }
        return listOfTrailers;
    }
    
    
    private static ContentValues[] getContentValuesFromMovieReviewLookup(Response<Reviews> response) {
        List<ReviewResult> reviewResultList = response.body().results;
        ContentValues[] listOfReviews = new ContentValues[reviewResultList.size()];
        ContentValues movieReviews;
        for (int index = 0; index < reviewResultList.size(); index++) {
            movieReviews = new ContentValues();
            movieReviews.put(MovieContract.MovieReview.COLUMN_MOVIEID, response.body().id);
            movieReviews.put(MovieContract.MovieReview.COLUMN_REVIEW_AUTHOR, reviewResultList.get(index).author);
            movieReviews.put(MovieContract.MovieReview.COLUMN_REVIEW_CONTENT, reviewResultList.get(index).content);
            listOfReviews[index] = movieReviews;
        }
        return listOfReviews;
    }
    
    
    
    private static ContentValues getContentValuesFromAMovieLookup(Response<AMovie> response) {
        AMovie movie = response.body();
    
       ContentValues movieContentValue = new ContentValues();
        movieContentValue.put((MovieContract.MovieBase.COLUMN_MOVIEID), movie.getId());
        movieContentValue.put((MovieContract.Movie.COLUMN_DURATION), movie.getRuntime());

        
//        for (int index = 0; index < movieResultList.size(); index++) {
//            movie = new ContentValues();
//            movie.put(MovieContract.MovieBase.COLUMN_MOVIEID, movieResultList.get(index).id);
//            movie.put(MovieContract.MovieBase.COLUMN_ORIGINALTITLE, movieResultList.get(index).originalTitle);
//            movie.put(MovieContract.MovieBase.COLUMN_OVERVIEW, movieResultList.get(index).overview);
//            movie.put(MovieContract.MovieBase.COLUMN_POSTERPATH, movieResultList.get(index).posterPath);
//            movie.put(MovieContract.MovieBase.COLUMN_RELEASEDATE, movieResultList.get(index).releaseDate);
//            movie.put(MovieContract.MovieBase.COLUMN_VOTEAVERAGE, movieResultList.get(index).voteAverage);
//            listOfMovies[index] = movie;
//        }
        
        return movieContentValue;
    }
}
