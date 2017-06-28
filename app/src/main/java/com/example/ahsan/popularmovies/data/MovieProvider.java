package com.example.ahsan.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.ahsan.popularmovies.Utilities.MoviePreferences;

import java.util.Iterator;

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
    private MovieDBHelper movieDBHelper;
    
    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.MovieTopRated.PATH, CODE_MOVIES_TOPRATED);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.MoviePopular.PATH, CODE_MOVIES_POPULAR);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.FAVORITES_PATH, CODE_MOVIES_FAVORITES);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.MovieTrailers.PATH, CODE_TRAILERS);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.MovieReview.PATH, CODE_REVIEWS);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.Movie.PATH, CODE_MOVIES);
        
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
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
           case CODE_MOVIES:
                cursor = movieDBHelper.getReadableDatabase().query(
                        MovieContract.Movie.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_TRAILERS:
                cursor = movieDBHelper.getReadableDatabase().query(
                        MovieContract.MovieTrailers.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_REVIEWS:
                cursor = movieDBHelper.getReadableDatabase().query(
                        MovieContract.MovieReview.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_MOVIES_POPULAR:
                cursor = movieDBHelper.getReadableDatabase().query(
                        MovieContract.MoviePopular.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_MOVIES_TOPRATED:
                cursor = movieDBHelper.getReadableDatabase().query(
                        MovieContract.MovieTopRated.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_MOVIES_FAVORITES:
                //using query builder http://blog.cubeactive.com/android-creating-a-join-with-sqlite/
                Iterator<String> favIterator =  MoviePreferences.getFavoritesSet(getContext()).iterator();
                String query = "";
                String sqltablepopular = null;
                String sqltabletoprated = null;
                
                String favoriteMovieId = null;
                StringBuilder listIDs = new StringBuilder(query);
                 while (favIterator.hasNext()) {
                    favoriteMovieId = favIterator.next();
                     listIDs.append(favoriteMovieId);
                    if (favIterator.hasNext())
                        listIDs.append(",");
                }
                sqltablepopular = "Select * from " + MovieContract.MoviePopular.TABLE_NAME + " where " +
                        MovieContract.MoviePopular.TABLE_NAME + "." + MovieContract.MoviePopular.COLUMN_MOVIEID + " IN(" + listIDs + ")";
                sqltabletoprated = "Select * from " + MovieContract.MovieTopRated.TABLE_NAME + " where " +
                        MovieContract.MovieTopRated.TABLE_NAME + "." + MovieContract.MovieTopRated.COLUMN_MOVIEID + " IN(" + listIDs + ")";
                if (sqltablepopular == null) {
                    return null;
                }
                query = sqltablepopular + " UNION " + sqltabletoprated;
                cursor = movieDBHelper.getReadableDatabase().rawQuery(query, null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        
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
             case CODE_MOVIES:
                 db.beginTransaction();
                 long _id;
                 try {
                     _id = db.insert(MovieContract.Movie.TABLE_NAME, null, values);
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
                        long _id = db.insert(MovieContract.MoviePopular.TABLE_NAME, null, value);
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
                        long _id = db.insert(MovieContract.MovieTopRated.TABLE_NAME, null, value);
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
            case CODE_TRAILERS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieTrailers.TABLE_NAME, null, value);
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
            case CODE_REVIEWS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieReview.TABLE_NAME, null, value);
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
        return 0;
    }
}
