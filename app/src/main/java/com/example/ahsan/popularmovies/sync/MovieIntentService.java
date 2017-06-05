package com.example.ahsan.popularmovies.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by Ahsan on 2017-05-29.
 */

public class MovieIntentService extends IntentService {
  
    public MovieIntentService() {
        super(MovieIntentService.class.getName());
    }
    
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        
        int action = intent.getIntExtra(MovieUtils.ACTION_LOOKUP,-1);
        int movieid = intent.getIntExtra(MovieUtils.MOVIE_ID,-1);
         MovieSyncTask.syncMovies(this,action, movieid);
        //friday harbour
    }
}
