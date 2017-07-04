package com.example.ahsan.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.ahsan.popularmovies.sync.MovieSyncTask;

/**
 * Created by Ahsan on 2017-05-29.
 */

public class MovieProvider extends ContentProvider {
    
    private static final int CODE_MOVIES_POPULAR = 100;
    private static final int CODE_MOVIES_TOPRATED = 200;
    private static final int CODE_REVIEWS = 300;
    private static final int CODE_TRAILERS = 400;
    private static final int CODE_MOVIES_FAVORITES = 500;
    private static final int CODE_MOVIES = 10;
    
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final int CODE_MOVIES_ALL = 20;
    private MovieDBHelper movieDBHelper;
    
    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
         matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.FAVORITES_PATH, CODE_MOVIES_FAVORITES);
         matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.MovieBase.PATH, CODE_MOVIES_ALL);
         return matcher;
    }
    
    @Override
    public boolean onCreate() {
        
        movieDBHelper = new MovieDBHelper(getContext());
        return true;
    }
    
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor = null;
        switch (sUriMatcher.match(uri)) {
           case CODE_MOVIES_ALL:
                cursor = movieDBHelper.getReadableDatabase().query(
                        MovieContract.MovieBase.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
               cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
 
            default:
                throw new UnsupportedOperationException("Unknown URI " + uri);
        }

        
        return cursor;
    }
    
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
    
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = movieDBHelper.getWritableDatabase();
         switch (sUriMatcher.match(uri)) {
             case CODE_MOVIES_ALL:
                 db.beginTransaction();
                 long _id;
                 try {
                     _id = db.insert(MovieContract.MovieBase.TABLE_NAME, null, values);
                     if (_id != -1) {
                         db.setTransactionSuccessful();
    
                     }
            
                  } finally {
                     db.endTransaction();
                 }
        
        
                 if (_id != -1) {
                     getContext().getContentResolver().notifyChange(uri, null);
                 }
        
                 return uri;
    
             default:
                 throw new UnsupportedOperationException("Unknown URI for Insert" + uri);
    
         }
    }
    
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = movieDBHelper.getWritableDatabase();
        int rowsInserted = 0;
        
        switch (sUriMatcher.match(uri)) {
            
            case CODE_MOVIES_POPULAR:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieBase.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                
                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                
                return rowsInserted;
            case CODE_MOVIES_TOPRATED:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieBase.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                
                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                
                return rowsInserted;
 
            default:
                return super.bulkInsert(uri, values);
        }
        
    }
    
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
    
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = movieDBHelper.getWritableDatabase();
        int rowsInserted = 0;
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIES_FAVORITES:
                db.beginTransaction();
                try {
                    if( (int) values.get("MOVIE_TYPE") == MovieSyncTask.POPULAR){
                        values.remove("MOVIE_TYPE");
                        long _id = db.update(MovieContract.MovieBase.TABLE_NAME, values,selection,selectionArgs);
                        if (_id != -1) {
                            rowsInserted++;
                        }
    
                    }else if( (int) values.get("MOVIE_TYPE") == MovieSyncTask.TOP_RATED){
                        values.remove("MOVIE_TYPE");
                        long _id = db.update(MovieContract.MovieBase.TABLE_NAME, values,selection,selectionArgs);
                        if (_id != -1) {
                            rowsInserted++;
                        }
    
                    }
                    db.setTransactionSuccessful();
                        
                } finally {
                    db.endTransaction();
                }
    
                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
    
                
        }
        return rowsInserted;
    }
}
