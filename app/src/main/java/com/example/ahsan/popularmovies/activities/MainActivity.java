package com.example.ahsan.popularmovies.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.ahsan.popularmovies.R;
import com.example.ahsan.popularmovies.adapters.ImageAdapter;
import com.example.ahsan.popularmovies.model.Movie;
import com.example.ahsan.popularmovies.webservices.FetchConfiguration;
import com.example.ahsan.popularmovies.webservices.FetchData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    JSONArray arrayOfMovies;

    public    boolean    loading = true;


    public String imageBaseURL = "";
    public static final String SORTBY_TOP_RATED = "SORTBY_TOP_RATED";
    public static final String SORTBY_POPULAR = "SORTBY_POPULAR";
    private ArrayList<Movie> movies;
    private ImageAdapter myAdapter;
    private GridView gridview;
    private boolean valid = false;
    public enum MovieResponse{
        TITLE("original_title"),
        THUMBNAIL("poster_path"),
        OVERVIEW("overview"),
        RELEASE_DATE("release_date"),
        RATING("vote_average");

        String value;
        MovieResponse(String title) {
            value = title;
        }
    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FetchConfiguration configuration = new FetchConfiguration(this);


        if(imageBaseURL.equals("") && isOnline())
        {
            configuration.execute();
        }

        FetchData movieDataService = new FetchData(this);

        if( isOnline()){
            movieDataService.execute("0",SORTBY_TOP_RATED);
        }else
            Toast.makeText(this, "Please verify your internet connection, and try again", Toast.LENGTH_SHORT).show();


        gridview = (GridView) findViewById(R.id.movie_grid_view);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Movie aMovie =  (Movie)parent.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this,DetailsActivity.class);
                intent.putExtra(MovieResponse.TITLE.name(),aMovie.getTitle());
                intent.putExtra(MovieResponse.OVERVIEW.name(),aMovie.getOverview());
                intent.putExtra(MovieResponse.RATING.name(),aMovie.getUserRating());
                intent.putExtra(MovieResponse.RELEASE_DATE.name(),aMovie.getReleaseDate());
                intent.putExtra(MovieResponse.THUMBNAIL.name(), imageBaseURL +aMovie.getImageBackground());
                startActivity(intent);

            }
        });




    }


    public String getImageBaseURL() {
        return imageBaseURL;
    }
    public void setData(JSONArray result) throws JSONException {
        arrayOfMovies = result;

        movies = new ArrayList<>();
        movies.clear();
        for(int i=0; i< arrayOfMovies.length(); i++){
            JSONObject aMovie = arrayOfMovies.getJSONObject(i);
            Movie movie =  new Movie();
            movie.setTitle(aMovie.getString(MovieResponse.TITLE.value));
            movie.setImageBackground(aMovie.getString(MovieResponse.THUMBNAIL.value));
            movie.setOverview(aMovie.getString(MovieResponse.OVERVIEW.value));
            movie.setReleaseDate(aMovie.getString(MovieResponse.RELEASE_DATE.value));
            movie.setUserRating(Float.parseFloat(aMovie.getString(MovieResponse.RATING.value)));
            movies.add(movie);
        }
        myAdapter = new ImageAdapter(this,movies);

        gridview.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();


    }

    public void setImageBaseURL(String imageBaseURL) {
        this.imageBaseURL = imageBaseURL;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.sort, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_top_rated) {
            FetchData movieDataService = new FetchData(this);
            movieDataService.execute("0",SORTBY_TOP_RATED);
            return true;
        }

        if (id == R.id.action_popularity) {
            FetchData movieDataService = new FetchData(this);
            movieDataService.execute("0",SORTBY_POPULAR);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
 /*   public class EndlessScrollListener implements GridView.OnScrollListener {

        private int visibleThreshold =0;
        private int currentPage = 0;
        private int previousTotal = 0;

        public EndlessScrollListener() {
        }
        public EndlessScrollListener(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                    currentPage++;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                // I load the next page of gigs using a background task,
                // but you can call any function here.

                FetchData movieDataService = new FetchData(MainActivity.this);
                movieDataService.execute(currentPage+1+"",SORTBY_TOP_RATED);
                 loading = true;
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    }*/

}
