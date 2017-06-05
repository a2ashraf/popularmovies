package com.example.ahsan.popularmovies.webservices;

import com.example.ahsan.popularmovies.model.Configuration;
import com.example.ahsan.popularmovies.model.Movies;
import com.example.ahsan.popularmovies.model.Reviews;
import com.example.ahsan.popularmovies.model.Trailers;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by Ahsan on 2017-05-28.
 */

public class RemoteMoviesAPI implements IRemoteMoviesAPI {
     private static final String BASE_URL = "https://api.themoviedb.org/3/";
    
    protected static Retrofit retrofit;
    
    private static RemoteMoviesAPI sInstance;
    private IRemoteMoviesAPI mRemoteAPI;
    private Map<String, String> queryParams;
    
    public RemoteMoviesAPI() {
        queryParams = new HashMap<String, String>();
        mRemoteAPI = retrofit.create(IRemoteMoviesAPI.class);
        queryParams.put("api_key", "07eb751564c8204d13c99ac50f3dd233");
        queryParams.put("page", "1");
        queryParams.put("language", "en-US");
        
    }
    
    private Retrofit getRetrofit() {
        return retrofit;
    }
    
    public static IRemoteMoviesAPI getInstance() {
        if (sInstance == null) {
            
            
            retrofit = new Retrofit.Builder()
                    .baseUrl(getBaseUrl())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            sInstance = new RemoteMoviesAPI();
        }
        
        
        return sInstance;
        
    }
    
    public static String getBaseUrl() {
        return BASE_URL;
    }
    
    
    @Override
    public Call<Movies> getTopRated(@QueryMap Map<String, String> options) {
        options = queryParams;
        return mRemoteAPI.getTopRated(options);
    }
    
    @Override
    public Call<Movies> getPopular(@QueryMap Map<String, String> options) {
        options = queryParams;
        return mRemoteAPI.getPopular(options);
    }
    
    @Override
    public Call<Reviews> getReviews(@Path("movie_id") String movie_id, @QueryMap Map<String, String> options) {
        options = queryParams;
        return mRemoteAPI.getReviews(movie_id,options);
    }
    
    @Override
    public Call<Trailers> getVideos(@Path("movie_id") String movie_id, @QueryMap Map<String, String> options) {
        options = queryParams;
        return mRemoteAPI.getVideos(movie_id,options);
    }
    
    @Override
    public Call<Configuration> getConfiguration(@QueryMap Map<String, String> options) {
        options=queryParams;
        return mRemoteAPI.getConfiguration(options);
    }
}
