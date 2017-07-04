package com.example.ahsan.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Ahsan on 2017-05-29.
 */

public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.example.ahsan.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String FAVORITES_PATH = "favorites";
    public static final Uri FAVORITES_URI = BASE_CONTENT_URI.buildUpon().appendPath(FAVORITES_PATH).build();
    
    
    public interface MovieBase extends BaseColumns {
        String PATH = "movies";
        Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();
        String COLUMN_POSTERPATH = "posterpath";
        String COLUMN_OVERVIEW = "overview";
        String COLUMN_RELEASEDATE = "releasedate";
        String COLUMN_ORIGINALTITLE = "originaltitle";
        String COLUMN_VOTEAVERAGE = "voteaverage";
        String COLUMN_MOVIEID = "movieid";
        String COLUMN_FAVORITES = "favorites";
        String TABLE_NAME = "movies";
        String COLUMN_TABLE_NAME = "sourcedata";
        
    }
    
    
    public static final class Movie implements MovieBase {
        public static final String PATH = "movie";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();
        public static final String TABLE_NAME = "amovie";
        
        public static String COLUMN_DURATION = "duration";
    }
    
    
}
