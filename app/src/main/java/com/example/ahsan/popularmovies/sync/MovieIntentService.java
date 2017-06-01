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
        MovieSyncTask.syncMovies(this);
    }
}
