package com.example.ahsan.popularmovies.fragments;

import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.example.ahsan.popularmovies.Utilities.NetworkUtil;

import static com.orhanobut.logger.Logger.d;

/**
 * Created by Ahsan on 2017-05-28.
 */

public abstract class BaseFragment extends Fragment {
    
    boolean forceLoad = false;
     
    @Override
    public void onResume() {
        d(" ");
        super.onResume();
 
        
        if (NetworkUtil.isOnline(getActivity())) {
            
            makeRequest(forceLoad);
        } else
            Toast.makeText(getActivity(), "Please verify your internet connection, and try again", Toast.LENGTH_SHORT).show();
        
        
    }
    
   
    
    public abstract void makeRequest(boolean forceLoad);
    
}
