package com.example.ahsan.popularmovies.activities;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.ahsan.popularmovies.R;
import com.example.ahsan.popularmovies.Utilities.MoviePreferences;
import com.example.ahsan.popularmovies.fragments.MovieDetails;
import com.example.ahsan.popularmovies.fragments.MovieListing;
import com.example.ahsan.popularmovies.model.Configuration;
import com.example.ahsan.popularmovies.model.Images;
import com.example.ahsan.popularmovies.sync.MovieUtils;
import com.example.ahsan.popularmovies.webservices.RemoteMoviesAPI;
import com.facebook.stetho.Stetho;
import com.orhanobut.logger.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MovieListing.OnFragmentInteractionListener {
    
    
    public Images imageOptions;
    private FragmentManager fm;
    private FragmentTransaction fragmentTransaction;
    private String THISTAG = "ADVANCEMENT";
    private MovieListing movieListingRated;
    public static final int INVALID_VALUE = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Logger.init(THISTAG).methodCount(6);
        Stetho.initializeWithDefaults(this);
        if (savedInstanceState != null) {
            return;
        }
        
        //this will go and sync the local database.
        MovieUtils.initialize(this,MovieUtils.ACTION_LOOKUP_MOVIES,INVALID_VALUE);
        RemoteMoviesAPI.getInstance().getConfiguration(null).enqueue(new Callback<Configuration>() {
            
            
            @Override
            public void onResponse(Call<Configuration> call, Response<Configuration> response) {
        /*set up preferences!*/
                imageOptions = response.body().images;
                setImageOptions(imageOptions);
                
            }
            
            @Override
            public void onFailure(Call<Configuration> call, Throwable t) {
                Logger.d(t.getMessage());
                new Throwable(t);
            }
        });
        
        
        if (findViewById(R.id.fragment_container) != null) {
            
            movieListingRated = MovieListing.newInstance(MovieListing.MOVIE_TYPE_TOP_RATED);
//           MovieListing movieListingPopular = MovieListing.newInstance(MovieListing.MOVIE_TYPE_POPULAR);
            fm = getSupportFragmentManager();
            fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, movieListingRated);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
            
        }
    }
    
    
    @Override
    protected void onPause() {
        super.onPause();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    
    
    
    @Override
    public void onFragmentInteraction(Bundle bundle) {
        Logger.d("ButtonClicked");
        MovieDetails movieDetails = MovieDetails.newInstance(bundle);
        if (movieDetails != null) {
            fm = getSupportFragmentManager();
            fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, movieDetails);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else
            Toast.makeText(this, "Something went wrong with network call, no bundle found", Toast.LENGTH_SHORT);
        
    }
    
    @Override
    public Images getImageOption() {
        return getImageOptions();
    }
    
    public Images getImageOptions() {
        return imageOptions;
    }
    
    public void setImageOptions(Images imageOptions) {
    
        if (imageOptions != null) {
            MoviePreferences.setBackdropSize(this.getApplicationContext(), imageOptions.backdropSizes.get(3));
            MoviePreferences.setImageBaseURL(this.getApplicationContext(), imageOptions.secureBaseUrl);
        }
        this.imageOptions = imageOptions;
    }
    
    
}
