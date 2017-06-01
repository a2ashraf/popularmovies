package com.example.ahsan.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Ahsan on 2017-05-29.
 */

public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.example.ahsan.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    

    
    public interface  Movie  extends BaseColumns {
        public static final String COLUMN_POSTERPATH = "posterpath";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASEDATE = "releasedate";
        public static final String COLUMN_ORIGINALTITLE = "originaltitle";
        public static final String COLUMN_VOTEAVERAGE = "voteaverage";
        public static final String COLUMN_MOVIEID = "movieid";
    
    }
    
    public static final class MoviePopular implements Movie {
        public static final String PATH= "popular";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();
        public static final String TABLE_NAME = "popularmovies";
    }
    
    public static final class MovieTopRated implements Movie {
        public static final String TABLE_NAME = "topratedmovies";
        public static final String PATH= "toprated";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();
    }
    
    
    public static final class MovieReview implements BaseColumns{
        public static final String TABLE_NAME = "moviereviews";
        public static final String PATH= "reviews";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();
    
    }
    
    public static final class MovieTrailers implements BaseColumns{
        public static final String TABLE_NAME = "trailers";
        public static final String PATH = "trailers";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();
    }
    
    
}
