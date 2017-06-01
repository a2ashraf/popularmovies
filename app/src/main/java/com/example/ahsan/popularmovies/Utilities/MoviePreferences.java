package com.example.ahsan.popularmovies.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Ahsan on 2017-05-31.
 */

public class MoviePreferences {
    
    public static final String PREFS_NAME = "movie-preferences";
    public static final String PREF_IMAGE_BASE_URL= "image-base-url";
    public static final String BACKDROP_SIZE= "backdrop-size";
    public static final String DEFAULT_BACKDROP_SIZE = "w500";
    
    public static void removePreferenceValues(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME,Context.MODE_APPEND);
        SharedPreferences.Editor editor = sp.edit();
        
        editor.remove(PREF_IMAGE_BASE_URL);
        editor.apply();
    }
    
    
    public static void setImageBaseURL(Context context, String url) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME,Context.MODE_APPEND);
        SharedPreferences.Editor editor = sp.edit();
    
        editor.putString(PREF_IMAGE_BASE_URL, url);
        editor.apply();
    }
    
    public static String getBaseUrl(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME,Context.MODE_APPEND);
        return sp.getString(PREF_IMAGE_BASE_URL,null);
    }
    
    public static void setBackdropSize(Context context, String url) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME,Context.MODE_APPEND);
        SharedPreferences.Editor editor = sp.edit();
        
        editor.putString(BACKDROP_SIZE, url);
        editor.apply();
    }
    
    public static String getBackDropSize(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME,Context.MODE_APPEND);
        return sp.getString(BACKDROP_SIZE,DEFAULT_BACKDROP_SIZE);
    }
    
}
