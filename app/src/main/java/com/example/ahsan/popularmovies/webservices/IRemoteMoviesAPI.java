package com.example.ahsan.popularmovies.webservices;

import com.example.ahsan.popularmovies.model.Configuration;
import com.example.ahsan.popularmovies.model.Movies;
import com.example.ahsan.popularmovies.model.Reviews;
import com.example.ahsan.popularmovies.model.Trailers;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by Ahsan on 2017-05-28.
 */

public interface IRemoteMoviesAPI {
    
    public static String CONTENTN_JSON = "Content-Type: application/json";
    
    
 
    //@Headers(CONTENTN_JSON)
    @GET("movie/top_rated")
    Call<Movies> getTopRated(@QueryMap Map<String, String> options);
    
//    @Headers(CONTENTN_JSON)
    @GET("movie/popular")
    Call<Movies> getPopular(@QueryMap Map<String, String> options);
    
    @GET("movie/{movie_id}/reviews")
    Call<Reviews> getReviews(@Path("movie_id") String movie_id, @QueryMap Map<String, String> options);
    
    @GET("movie/{movie_id}/videos")
    Call<Trailers> getVideos(@Path("movie_id") String movie_id, @QueryMap Map<String, String> options);
    
    @GET("configuration")
    Call<Configuration> getConfiguration(@QueryMap Map<String, String> options);

//    @Headers(CONTENTN_JSON)
//    @GET("/movie/top_rated")
//    Call<ResponseBody> top_rated(@Header(AUTHORIZATION) String authToken);
//
//
    
}
