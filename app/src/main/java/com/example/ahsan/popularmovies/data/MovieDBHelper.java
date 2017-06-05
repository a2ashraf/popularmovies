package com.example.ahsan.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ahsan on 2017-05-29.
 */

public class MovieDBHelper extends SQLiteOpenHelper {
   
   private static String DB_NAME = "movies.db";
   private static int VERSION= 2;
    
   
    public MovieDBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
         //here we create the table(s).
        final String SQL_CREATE_MOVIE_POPULAR_TABLE =
            
                "CREATE TABLE " + MovieContract.MoviePopular.TABLE_NAME + " (" +
                        MovieContract.MoviePopular._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        
                        MovieContract.MoviePopular.COLUMN_MOVIEID       + " INTEGER NOT NULL, "                 +
        
                        MovieContract.MoviePopular.COLUMN_ORIGINALTITLE + " INTEGER NOT NULL,"                  +
        
                        MovieContract.MoviePopular.COLUMN_OVERVIEW   + " REAL NOT NULL, "                    +
                        MovieContract.MoviePopular.COLUMN_POSTERPATH   + " REAL NOT NULL, "                    +
        
                        MovieContract.MoviePopular.COLUMN_RELEASEDATE   + " REAL NOT NULL, "                    +
                        MovieContract.MoviePopular.COLUMN_VOTEAVERAGE   + " REAL NOT NULL, "                    +
                        MovieContract.MoviePopular.COLUMN_FAVORITES   + " REAL, "                    +
                        
                        " UNIQUE (" +  MovieContract.MoviePopular.COLUMN_MOVIEID  + ") ON CONFLICT REPLACE);";
    
        final String SQL_CREATE_MOVIE_TOP_RATED_TABLE =
            
                "CREATE TABLE " + MovieContract.MovieTopRated.TABLE_NAME + " (" +
                        MovieContract.MovieTopRated._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    
                        MovieContract.MovieTopRated.COLUMN_MOVIEID       + " INTEGER NOT NULL, "                 +
                    
                        MovieContract.MovieTopRated.COLUMN_ORIGINALTITLE + " INTEGER NOT NULL,"                  +
                    
                        MovieContract.MovieTopRated.COLUMN_OVERVIEW   + " REAL NOT NULL, "                    +
                        MovieContract.MovieTopRated.COLUMN_POSTERPATH   + " REAL NOT NULL, "                    +
                    
                        MovieContract.MovieTopRated.COLUMN_RELEASEDATE   + " REAL NOT NULL, "                    +
                        MovieContract.MovieTopRated.COLUMN_VOTEAVERAGE   + " REAL NOT NULL, "                    +
                        MovieContract.MovieTopRated.COLUMN_FAVORITES   + " REAL, "                    +
                        
                        " UNIQUE (" +  MovieContract.MovieTopRated.COLUMN_MOVIEID  + ") ON CONFLICT REPLACE);";
    
        final String SQL_CREATE_MOVIE_REVIEWS_TABLE =
            
                "CREATE TABLE " + MovieContract.MovieReview.TABLE_NAME + " (" +
                        MovieContract.MovieReview._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieContract.MovieReview.COLUMN_MOVIEID       + " INTEGER NOT NULL, "                 +
                        MovieContract.MovieReview.COLUMN_REVIEW_AUTHOR       + " REAL NOT NULL, "                 +
                        MovieContract.MovieReview.COLUMN_REVIEW_CONTENT       + " REAL NOT NULL, "                 +
                    
                        " UNIQUE (" +  MovieContract.MovieReview.COLUMN_MOVIEID  + ") ON CONFLICT REPLACE);";
    
    
        final String SQL_CREATE_MOVIE_TRAILERS_TABLE =
            
                "CREATE TABLE " + MovieContract.MovieTrailers.TABLE_NAME + " (" +
                        MovieContract.MovieTrailers._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieContract.MovieTrailers.COLUMN_MOVIEID       + " INTEGER NOT NULL, "                 +
                        MovieContract.MovieTrailers.COLUMN_TRAILER_KEY       + " INTEGER NOT NULL, "                 +
                        " UNIQUE (" +  MovieContract.MovieTrailers.COLUMN_MOVIEID  + ") ON CONFLICT REPLACE);";
        
        db.execSQL(SQL_CREATE_MOVIE_REVIEWS_TABLE);
        db.execSQL(SQL_CREATE_MOVIE_TRAILERS_TABLE);
        db.execSQL(SQL_CREATE_MOVIE_TOP_RATED_TABLE);
        db.execSQL(SQL_CREATE_MOVIE_POPULAR_TABLE);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MoviePopular.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieTopRated.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
