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
    public static final Uri FAVORITES_URI =  BASE_CONTENT_URI.buildUpon().appendPath(FAVORITES_PATH).build();
    
    
    
    public interface MovieBase extends BaseColumns {
        public static final String COLUMN_POSTERPATH = "posterpath";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASEDATE = "releasedate";
        public static final String COLUMN_ORIGINALTITLE = "originaltitle";
        public static final String COLUMN_VOTEAVERAGE = "voteaverage";
        public static final String COLUMN_MOVIEID = "movieid";
        public static final String COLUMN_FAVORITES = "favorites";
        
    }
    
    
    public static final class Movie implements MovieBase {
        public static final String PATH= "movie";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();
        public static final String TABLE_NAME = "amovie";
    
        public static String COLUMN_DURATION = "duration";
    }
    
    public static final class MoviePopular implements MovieBase {
        public static final String PATH= "popular";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();
        public static final String TABLE_NAME = "popularmovies";
        
    }
    
    public static final class MovieTopRated implements MovieBase {
        public static final String TABLE_NAME = "topratedmovies";
        public static final String PATH= "toprated";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();
    }
    
    
    public static final class MovieReview implements BaseColumns{
        public static final String TABLE_NAME = "moviereviews";
        public static final String PATH= "reviews";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();
        public static final String COLUMN_REVIEW_CONTENT = "content";
        public static final String COLUMN_REVIEW_AUTHOR = "author";
        public static final String COLUMN_MOVIEID = "movieid";
    }
    
    public static final class MovieTrailers implements BaseColumns{
        public static final String TABLE_NAME = "trailers";
        public static final String PATH = "trailers";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();
        public static final String COLUMN_TRAILER_KEY = "trailerurl";
        public static final String COLUMN_MOVIEID = "movieid";
    
    
    }
    
    
}
