package com.example.ahsan.popularmovies.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;

/**
 * Created by Ahsan on 2017-05-31.
 */

public class MoviePreferences {
    
    public static final String PREFS_NAME = "movie-preferences";
    public static final String PREF_IMAGE_BASE_URL= "image-base-url";
    public static final String BACKDROP_SIZE= "backdrop-size";
    public static final String DEFAULT_BACKDROP_SIZE = "w500";
    public static final String FAVORITES = "favorites";
    
    public static void removePreferenceValues(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        
        editor.remove(PREF_IMAGE_BASE_URL);
        editor.commit();
    }
    
    
    public static void setImageBaseURL(Context context, String url) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
    
        editor.putString(PREF_IMAGE_BASE_URL, url);
        editor.commit();
    }
    
    public static String getBaseUrl(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        return sp.getString(PREF_IMAGE_BASE_URL,null);
    }
    
    public static void setBackdropSize(Context context, String url) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        
        editor.putString(BACKDROP_SIZE, url);
        editor.commit();
    }
    
    public static String getBackDropSize(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        return sp.getString(BACKDROP_SIZE,DEFAULT_BACKDROP_SIZE);
    }
    
    public static void addToFavorites(Context context, String movieId) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        HashSet<String> favoritesSet = (HashSet<String>) sp.getStringSet(FAVORITES,new HashSet<String>());
        favoritesSet.add(movieId);
        editor.putStringSet(FAVORITES,favoritesSet );
        
        editor.commit();
    }
    
    public static void removeFromFavorites(Context context, String movieId) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        HashSet<String> favoritesSet = (HashSet<String>) sp.getStringSet(FAVORITES,new HashSet<String>());
        
        if(favoritesSet.contains(movieId)){
            favoritesSet.remove(movieId);
            editor.putStringSet(FAVORITES,favoritesSet);
            editor.commit();
        }
        
    }
    
    
    public static HashSet<String> getFavoritesSet(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        return (HashSet<String>) sp.getStringSet(FAVORITES,new HashSet<String>());
    }
    
    public static boolean isInFavorites(Context context, String movieId) {
       
        HashSet<String> favorites = getFavoritesSet(context);
        if(favorites==null)
            return false;
        return favorites.contains(movieId);
    }
    
    
    
    
}
