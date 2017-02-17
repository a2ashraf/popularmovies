package com.example.ahsan.popularmovies.activities;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.example.ahsan.popularmovies.R;
import com.example.ahsan.popularmovies.fragments.MovieDetails;
import com.example.ahsan.popularmovies.fragments.MovieListing;

public class MainActivity extends AppCompatActivity implements MovieListing.OnFragmentInteractionListener{


    private FragmentManager fm;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);
    }


    @Override
    protected void onResume() {
        super.onResume();
        MovieListing movieListing =  MovieListing.newInstance(null);
        fm = getSupportFragmentManager();
        fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container,movieListing);

        fragmentTransaction.commit();

    }


    @Override
    public void onFragmentInteraction(Bundle bundle) {
        //take new arguments AND then call the right fragment to be loaded.

        MovieDetails movieDetails=  MovieDetails.newInstance(bundle);
        fm = getSupportFragmentManager();
        fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,movieDetails);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
