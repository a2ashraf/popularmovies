package com.example.ahsan.popularmovies.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.ahsan.popularmovies.data.MovieContract;

/**
 * Created by Ahsan on 2017-05-29.
 */



//checks to see if data exists.  if not, it requests a sync.  if it exists (and updated) then it sets it up for loader to work.
    
public class MovieUtils {
    
    private static boolean initialized;
    
    synchronized public static void initialize(@NonNull final Context context){
        if(initialized) return;
        
        initialized = true;
    /*
    * check if one of the two tables exist, if not, we attempt to update database asap.
    * */
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                
                //check db connection, if none, go and start the sync.
    
                Uri moviesUri = MovieContract.MoviePopular.CONTENT_URI;
                String[] columnsProjection= {MovieContract.Movie.COLUMN_MOVIEID};
     
                Cursor cursor = context.getContentResolver().query(moviesUri,columnsProjection,null,null,null);
                if (cursor == null || cursor.getCount() == 0) {
                    startSyncWithWeb(context);
                }
                cursor.close();
    
                return null;
            }
        }.execute();
    }
    
    private static void startSyncWithWeb(Context context) {
        Intent syncWithWebIntent = new Intent(context,MovieIntentService.class);
        context.startService(syncWithWebIntent);
    }
}
