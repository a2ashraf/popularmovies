package com.example.ahsan.popularmovies.activities;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.ahsan.popularmovies.R;
import com.example.ahsan.popularmovies.fragments.MovieDetails;
import com.example.ahsan.popularmovies.fragments.MovieListing;
import com.orhanobut.logger.Logger;

public class MainActivity extends AppCompatActivity implements MovieListing.OnFragmentInteractionListener {


    private FragmentManager fm;
    private FragmentTransaction fragmentTransaction;
    private String THISTAG = "ADVANCEMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Logger.init(THISTAG).methodCount(6);

        if (savedInstanceState != null) {
            return;
        }

        if (findViewById(R.id.fragment_container) != null) {

            MovieListing movieListing = MovieListing.newInstance(null);
            fm = getSupportFragmentManager();
            fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, movieListing);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
        }
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
}
