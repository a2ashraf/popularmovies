package com.example.ahsan.popularmovies.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.ahsan.popularmovies.data.MovieContract;

/**
 * Created by Ahsan on 2017-05-29.
 */



//checks to see if data exists.  if not, it requests a sync.  if it exists (and updated) then it sets it up for loader to work.
    
public class MovieUtils {
    
    public static final String MOVIE_ID = "movie_id";
    public static final String ACTION_LOOKUP = "network_movie_action";
    private static boolean initialized;
    public static final int ACTION_LOOKUP_REVIEWS = 100;
    public static final int ACTION_LOOKUP_TRAILERS = 200;
    public static final int ACTION_LOOKUP_MOVIES = 300;
    
    
    synchronized public static void initialize(@NonNull final Context context, @Nullable final int action, @Nullable final int movieid){
        if(initialized) return;
        
        initialized = true;
    /*
    * check if one of the two tables exist, if not, we attempt to update database asap.
    * */
        new AsyncTask<Integer, Void, Void>() {
    
    
            
    
            @Override
            protected Void doInBackground(Integer... params) {
                Uri moviesUri = null;
//                String[] columnsProjection= {MovieContract.Movie.COLUMN_MOVIEID};
                String[] columnsProjection= null;
    
                //check db connection, if none, go and start the sync.
//                int count = params.length;
//                for(int x=0; x<count;x++){
//                    int ACTION  = params[x];
//                }
                String selection = null;
                String qualification[] = null;
                switch(action){
                    case(ACTION_LOOKUP_REVIEWS):
                       moviesUri = MovieContract.MoviePopular.CONTENT_URI;
                          selection = MovieContract.Movie.COLUMN_MOVIEID;
                          qualification= new String[] {String.valueOf(movieid)};
                             break;
                    case (ACTION_LOOKUP_TRAILERS):
                        moviesUri = MovieContract.MovieTrailers.CONTENT_URI;
                          selection = MovieContract.Movie.COLUMN_MOVIEID;
                          qualification= new String[] {String.valueOf(movieid)};
                         break;
                    case (ACTION_LOOKUP_MOVIES):
                         moviesUri = MovieContract.MovieReview.CONTENT_URI;
                         break;
                    default:
                        throw new UnsupportedOperationException("Unrewcognized URI:" + action);
                }

             
                Cursor cursor = context.getContentResolver().query(moviesUri,columnsProjection,selection,qualification,null);
                if (cursor == null || cursor.getCount() == 0) {
                    startSyncWithWeb(context, action, movieid
                    );
                }else{
                    
                }
                cursor.close();
    
                return null;
            }
        }.execute(action);
    }
    
    public static void startSyncWithWeb(Context context, int action, int movieId) {
        Intent syncWithWebIntent = new Intent(context,MovieIntentService.class);
        
         syncWithWebIntent.putExtra(MOVIE_ID, movieId);
         syncWithWebIntent.putExtra(ACTION_LOOKUP, action);
        context.startService(syncWithWebIntent);
    }
}
