package com.example.ahsan.popularmovies.activities;

import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ahsan.popularmovies.R;
import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        Bundle extras = getIntent().getExtras();
        String title = "";
        String overview= "";
        float rating = 0;
        String release= "";
        String thumbnail = null;

        if (extras != null) {
            title = extras.getString(MainActivity.MovieResponse.TITLE.name());
            overview = extras.getString(MainActivity.MovieResponse.OVERVIEW.name());
            rating = extras.getFloat(MainActivity.MovieResponse.RATING.name());
            thumbnail = extras.getString(MainActivity.MovieResponse.THUMBNAIL.name());
            release = extras.getString(MainActivity.MovieResponse.RELEASE_DATE.name());
            // and get whatever type user account id is
        }

        TextView title_view = (TextView)findViewById(R.id.title_of_movie);
        title_view.setText(title.toString());
        TextView overview_view = (TextView)findViewById(R.id.overview);
        overview_view.setText(overview.toString());

        TextView rating_view = (TextView)findViewById(R.id.rating);
        rating_view.setText(rating_view.getText().toString()+ "  " +rating );

        TextView release_view = (TextView)findViewById(R.id.release_date);
        release_view.setText(release_view.getText()+"  " + release.toString());

        final ImageView background_view = (ImageView) findViewById(R.id.background_image);
        final String finalThumbnail = thumbnail;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width_screen = size.x;
        int height_screen = size.y;
        background_view.setMinimumHeight(height_screen/2);
        background_view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
              int   width = background_view.getMeasuredWidth();
               int  height = background_view.getMeasuredHeight();
                Picasso.with(DetailsActivity.this).load(finalThumbnail) .into(background_view);
                background_view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

}
