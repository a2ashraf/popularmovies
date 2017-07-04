package com.example.ahsan.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ahsan on 2017-05-29.
 */

public class MovieDBHelper extends SQLiteOpenHelper {
   
   private static String DB_NAME = "movies.db";
     
   private static int VERSION= 3;
    
   
    public MovieDBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
         //here we create the table(s).
        final String SQL_CREATE_MOVIE_POPULAR_TABLE =
            
                "CREATE TABLE " + MovieContract.MovieBase.TABLE_NAME + " (" +
                        MovieContract.MovieBase._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        
                        MovieContract.MovieBase.COLUMN_MOVIEID       + " INTEGER NOT NULL, "                 +
        
                        MovieContract.MovieBase.COLUMN_ORIGINALTITLE + " INTEGER NOT NULL,"                  +
        
                        MovieContract.MovieBase.COLUMN_OVERVIEW   + " REAL NOT NULL, "                    +
                        MovieContract.MovieBase.COLUMN_POSTERPATH   + " REAL NOT NULL, "                    +
        
                        MovieContract.MovieBase.COLUMN_RELEASEDATE   + " REAL NOT NULL, "                    +
                        MovieContract.MovieBase.COLUMN_VOTEAVERAGE   + " REAL NOT NULL, "                    +
                        MovieContract.MovieBase.COLUMN_FAVORITES   + " INTEGER DEFAULT 0, "                    +
                        MovieContract.MovieBase.COLUMN_TABLE_NAME   + " INTEGER DEFAULT 0,"                    +
                                " UNIQUE (" +  MovieContract.MovieBase.COLUMN_MOVIEID  + ") ON CONFLICT REPLACE);";
    
//
        db.execSQL(SQL_CREATE_MOVIE_POPULAR_TABLE);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
             sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.Movie.TABLE_NAME);
       
        onCreate(sqLiteDatabase);
    }
}
