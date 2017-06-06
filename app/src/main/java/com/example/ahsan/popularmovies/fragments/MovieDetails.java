package com.example.ahsan.popularmovies.fragments;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahsan.popularmovies.R;
import com.example.ahsan.popularmovies.Utilities.BackButtonHandlerInterface;
import com.example.ahsan.popularmovies.Utilities.MoviePreferences;
import com.example.ahsan.popularmovies.Utilities.OnBackClickListener;
import com.example.ahsan.popularmovies.adapters.TrailerAdapter;
import com.example.ahsan.popularmovies.data.MovieContract;
import com.example.ahsan.popularmovies.enums.MovieResponse;
import com.example.ahsan.popularmovies.sync.MovieUtils;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import static com.example.ahsan.popularmovies.Utilities.MoviePreferences.DEFAULT_BACKDROP_SIZE;
import static com.example.ahsan.popularmovies.sync.MovieUtils.ACTION_LOOKUP_TRAILERS;

public class MovieDetails extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,OnBackClickListener {
    private static final int ID_MOVIE_REVIEWS = 6000;
    private static final int ID_MOVIE_TRAILERS = 5000;
    // TODO: Rename parameter arguments, choose names that match
    
    String title = "";
    String overview = "";
    String rating = "0";
    String release = "";
    String thumbnail = null;
    RecyclerView trailersRecycleView;
    private boolean isFavorite;
    private String movieId;
    private TrailerAdapter trailerAdapter;
    private int mPosition = RecyclerView.NO_POSITION;
    

    private BackButtonHandlerInterface backButtonHandler;
    private boolean fuck;
    private CursorLoader mainLoader;
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        backButtonHandler = (BackButtonHandlerInterface) context;
        backButtonHandler.addBackClickListener(this);
    }
    
    @Override
    public void onDetach() {
        super.onDetach();
        backButtonHandler.removeBackClickListener(this);
        backButtonHandler = null;
    }
    
    @Override
    public boolean onBackClick() {
          return false;
    }
    
    // TODO: Rename and change types and number of parameters
    public static MovieDetails newInstance(Bundle bundle) {
        MovieDetails fragment = new MovieDetails();
        Logger.d("loadFlow OncreateView - MovieDetails");
    
        Bundle args = new Bundle();
        if (bundle != null) {
            args.putAll(bundle);
            fragment.setArguments(args);
            return fragment;
        }
        return null;
        
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras;
        if (savedInstanceState != null)
            extras = savedInstanceState;
        else
            extras = this.getArguments();
    
        
        
       
    
        if (extras != null) {
            title = extras.getString(MovieResponse.TITLE.name());
            overview = extras.getString(MovieResponse.OVERVIEW.name());
            rating = extras.getString(MovieResponse.RATING.name());
            thumbnail = extras.getString(MovieResponse.POSTERPATH.name());
            release = extras.getString(MovieResponse.RELEASE_DATE.name());
            isFavorite = extras.getBoolean(MovieResponse.FAVORITE.name());
            movieId = extras.getString(MovieResponse.MOVIEID.name());
            
            //   getReviews();   // call the qury with values instead of null...
        
        }
    
        trailerAdapter = new TrailerAdapter(getContext(), Integer.parseInt(movieId));
    
    loadTrailers();
    
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Logger.d("OncreateView - MovieDetails");
       
       
        setHasOptionsMenu(false);
        View detailsView = inflater.inflate(R.layout.fragment_movie_details, container, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        trailersRecycleView = (RecyclerView) detailsView.findViewById(R.id.movie_trailers);
        trailersRecycleView.setHasFixedSize(true);
        trailersRecycleView.setLayoutManager(layoutManager);
        trailersRecycleView.setAdapter(trailerAdapter);
    
        final ImageView favorites = (ImageView) detailsView.findViewById(R.id.favorites);
        if (isFavorite) {
            favorites.setBackground(getResources().getDrawable(R.drawable.ic_favorite_white_24dp));
        } else {
            favorites.setBackground(getResources().getDrawable(R.drawable.ic_favorite_border_white_24dp));
        }
    
        favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFavorite) {
                    favorites.setBackground(getResources().getDrawable(R.drawable.ic_favorite_white_24dp));
                    Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();
                    MoviePreferences.addToFavorites(getContext().getApplicationContext(), movieId);
                
                } else {
                    favorites.setBackground(getResources().getDrawable(R.drawable.ic_favorite_border_white_24dp));
                    MoviePreferences.removeFromFavorites(getContext().getApplicationContext(), movieId);
                    Toast.makeText(getContext(), "Unsaved", Toast.LENGTH_SHORT).show();
                }
                isFavorite = !isFavorite;
                getContext().getContentResolver().notifyChange(MovieContract.FAVORITES_URI, null);
            }
        
        });
        TextView title_view = (TextView) detailsView.findViewById(R.id.title_of_movie);
        title_view.setText(title.toString());
        TextView overview_view = (TextView) detailsView.findViewById(R.id.overview);
        overview_view.setText(overview.toString());
    
        TextView rating_view = (TextView) detailsView.findViewById(R.id.rating);
        rating_view.setText(rating_view.getText().toString() + "  " + rating);
    
        TextView release_view = (TextView) detailsView.findViewById(R.id.release_date);
        release_view.setText(release_view.getText() + "  " + release.toString());
    
        final ImageView background_view = (ImageView) detailsView.findViewById(R.id.background_image);
        final String finalThumbnail = MoviePreferences.getBaseUrl(getContext()) + DEFAULT_BACKDROP_SIZE + thumbnail;
    
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height_screen = size.y;
        background_view.setMinimumHeight(height_screen / 2);
        background_view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
            
                Picasso.with(getActivity()).load(finalThumbnail).into(background_view);
                background_view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    
        TextView mvid = (TextView) detailsView.findViewById(R.id.id_movie);
        mvid.setText(movieId);
        return detailsView;
    }
    
     
    
    private void loadTrailers() {
       mainLoader = (CursorLoader) getActivity().getSupportLoaderManager().initLoader(ID_MOVIE_TRAILERS, null, this);
        
    }
 
    private void getReviews() {
        if (getActivity().getSupportLoaderManager().getLoader(ID_MOVIE_REVIEWS) == null)
            getActivity().getSupportLoaderManager().initLoader(ID_MOVIE_REVIEWS, null, this);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        MovieUtils.initialize(getContext(), ACTION_LOOKUP_TRAILERS,Integer.valueOf(movieId));
        getActivity().getSupportLoaderManager().restartLoader(ID_MOVIE_TRAILERS, null, this);
    }
    
    //put in adapter to play movie.
    
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        
        Uri uri = null;
        switch (id) {
            case (ID_MOVIE_REVIEWS):
                //return a cursor to this URI
                uri = MovieContract.MovieReview.CONTENT_URI;
                break;
            case (ID_MOVIE_TRAILERS):
                //return a cursor to this URI
                uri = MovieContract.MovieTrailers.CONTENT_URI;
                break;
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
            
            
        }
        String selection = MovieContract.Movie.COLUMN_MOVIEID + " = ? ";
        String[] qualification= new String[] {String.valueOf(movieId)};
        CursorLoader loader = new CursorLoader(getContext(), uri, null, selection, qualification, null);
        
        return loader;
    }
    
    
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        
        if (data == null) {
            return;
        }
        
        if (data.getCount() < 1 ) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    MovieUtils.startSyncWithWeb(getContext(), ACTION_LOOKUP_TRAILERS, Integer.parseInt(movieId));
                    return null;
                }
                
            }.execute();
        } else {
            trailerAdapter.swapCursor(data);
            trailerAdapter.setmId(Integer.parseInt(movieId));
            
            if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        
        if(trailersRecycleView!=null)    trailersRecycleView.smoothScrollToPosition(mPosition);
        }
    }
 
 
    
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        trailerAdapter.swapCursor(null);
    }
}
