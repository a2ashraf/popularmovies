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
import com.example.ahsan.popularmovies.adapters.ReviewAdapter;
import com.example.ahsan.popularmovies.adapters.TrailerAdapter;
import com.example.ahsan.popularmovies.data.MovieContract;
import com.example.ahsan.popularmovies.enums.MovieResponse;
import com.example.ahsan.popularmovies.sync.MovieUtils;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.example.ahsan.popularmovies.Utilities.MoviePreferences.DEFAULT_BACKDROP_SIZE;
import static com.example.ahsan.popularmovies.sync.MovieUtils.ACTION_LOOKUP_MOVIE;
import static com.example.ahsan.popularmovies.sync.MovieUtils.ACTION_LOOKUP_REVIEWS;
import static com.example.ahsan.popularmovies.sync.MovieUtils.ACTION_LOOKUP_TRAILERS;

public class MovieDetails extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, OnBackClickListener {
    private static final int ID_MOVIE_REVIEWS = 6000;
    private static final int ID_MOVIE_TRAILERS = 5000;
    private static final int ID_MOVIE = 7000;
    // TODO: Rename parameter arguments, choose names that match
    
    String title = "";
    String overview = "";
    String rating = "0";
    String release = "";
    String thumbnail = null;
    String durationTime;
    RecyclerView trailersRecycleView;
    RecyclerView reviewRecycleView;
    private boolean isFavorite;
    private String movieId;
    private TrailerAdapter trailerAdapter;
    private int mReviewPosition = RecyclerView.NO_POSITION;
    private TextView noReview;
    private TextView noTrailer;
    private TextView duration;
    
    private BackButtonHandlerInterface backButtonHandler;
     private ReviewAdapter reviewAdapter;
    private int mTrailerPosition = RecyclerView.NO_POSITION;
    
    
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
    public void onAttach(Context context) {
        super.onAttach(context);
        backButtonHandler = (BackButtonHandlerInterface) context;
        backButtonHandler.addBackClickListener(this);
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
            release = getReleaseYear(release);
        }
        
        trailerAdapter = new TrailerAdapter(getContext(), Integer.parseInt(movieId));
        reviewAdapter = new ReviewAdapter(getContext(), Integer.parseInt(movieId));
        //loadTrailers();
        //  loadReviews();
    }
    
    private String getReleaseYear(String release) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
        try {
            Date date = format.parse(release);
            Calendar releaseDate = Calendar.getInstance();
            releaseDate.setTime(date);
            return String.valueOf(releaseDate.get(Calendar.YEAR));
            
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Logger.d("OncreateView - MovieDetails");
        
        
        setHasOptionsMenu(false);
        View detailsView = inflater.inflate(R.layout.fragment_movie_details, container, false);
        LinearLayoutManager trailerlayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager reviewlayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        
        reviewRecycleView = (RecyclerView) detailsView.findViewById(R.id.movie_reviews);
        reviewRecycleView.setAdapter(reviewAdapter);
        reviewRecycleView.setLayoutManager(reviewlayoutManager);
        reviewRecycleView.setNestedScrollingEnabled(true);
        
        noReview = (TextView) detailsView.findViewById(R.id.textView_no_review);
        noTrailer = (TextView) detailsView.findViewById(R.id.textView_no_trailer);
        duration = (TextView) detailsView.findViewById(R.id.textView_duration);
        trailersRecycleView = (RecyclerView) detailsView.findViewById(R.id.movie_trailers);
        trailersRecycleView.setHasFixedSize(true);
        trailersRecycleView.setLayoutManager(trailerlayoutManager);
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
        rating_view.setText(rating + "/10");
        
        TextView release_view = (TextView) detailsView.findViewById(R.id.release_date);
        release_view.setText(release.toString());
        
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
    
    @Override
    public void onResume() {
        super.onResume();
        initLoader(ACTION_LOOKUP_REVIEWS);
        initLoader(ACTION_LOOKUP_MOVIE);
        initLoader(ACTION_LOOKUP_TRAILERS);
        getActivity().getSupportLoaderManager().restartLoader(ID_MOVIE_TRAILERS, null, this);
        getActivity().getSupportLoaderManager().restartLoader(ID_MOVIE, null, this);
        getActivity().getSupportLoaderManager().restartLoader(ID_MOVIE_REVIEWS, null, this);
    }
    
    @Override
    public void onDetach() {
        super.onDetach();
        backButtonHandler.removeBackClickListener(this);
        backButtonHandler = null;
    }
    
    private synchronized void initLoader(int actionLookupReviews) {
        switch (actionLookupReviews) {
            
            case (ACTION_LOOKUP_MOVIE):
                MovieUtils.initialize(getContext(), ACTION_LOOKUP_MOVIE, Integer.valueOf(movieId));
                break;
            case (ACTION_LOOKUP_TRAILERS):
                MovieUtils.initialize(getContext(), ACTION_LOOKUP_TRAILERS, Integer.valueOf(movieId));
                break;
            case (ACTION_LOOKUP_REVIEWS):
                MovieUtils.initialize(getContext(), ACTION_LOOKUP_REVIEWS, Integer.valueOf(movieId));
                break;
            default:
                throw new UnsupportedOperationException("Unrecognized action: " + actionLookupReviews);
        }
    }
    
    private void loadTrailers() {
        getActivity().getSupportLoaderManager().initLoader(ID_MOVIE_TRAILERS, null, this);
        
    }
    
    private void loadReviews() {
        getActivity().getSupportLoaderManager().initLoader(ID_MOVIE_REVIEWS, null, this);
    }
    
    @Override
    public boolean onBackClick() {
        return false;
    }
    
    //put in adapter to play movie.
    
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        
        Uri uri = null;
        switch (id) {
            
            case (ID_MOVIE):
                //return a cursor to this URI
                uri = MovieContract.Movie.CONTENT_URI;
                break;
            
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
        String selection = MovieContract.MovieBase.COLUMN_MOVIEID + " = ? ";
        String[] qualification = new String[]{String.valueOf(movieId)};
        CursorLoader loader = new CursorLoader(getContext(), uri, null, selection, qualification, null);
        
        return loader;
    }
    
    
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //get loader id/type and then based on that load the reviews/sync reviews etc
        if (data == null) {
            return;
        }
        
        if (ID_MOVIE_TRAILERS == loader.getId()) {
            if (data.getCount() < 1) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        MovieUtils.startSyncWithWeb(getContext(), ACTION_LOOKUP_TRAILERS, Integer.parseInt(movieId));
    
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showTrailerMessage();
                            }
                        });
                        return null;
                    }
                }.execute();
            } else if (data.getCount() > 0) {
                showTrailers();
                trailerAdapter.swapCursor(data);
                trailerAdapter.setmId(Integer.parseInt(movieId));
                
                if (mTrailerPosition == RecyclerView.NO_POSITION) mTrailerPosition = 0;
                
                if (trailersRecycleView != null) {
                    trailersRecycleView.smoothScrollToPosition(mTrailerPosition);
                }
            }
        } else if (ID_MOVIE == loader.getId()) {
            if (data.getCount() < 1) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        MovieUtils.startSyncWithWeb(getContext(), ACTION_LOOKUP_MOVIE, Integer.parseInt(movieId));
                        return null;
                    }
                    
                }.execute();
            } else if (data.getCount() > 0) {
                data.moveToFirst();
                int durationColumnIndex = data.getColumnIndex(MovieContract.Movie.COLUMN_DURATION);
                final String durationValue = data.getString(durationColumnIndex);
                duration.setText(durationValue + " min");
                
            }
        } else if (ID_MOVIE_REVIEWS == loader.getId()) {
            if (data.getCount() < 1) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        MovieUtils.startSyncWithWeb(getContext(), ACTION_LOOKUP_REVIEWS, Integer.parseInt(movieId));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showReviewDisplayMessage();
                            }
                        });

                        
                        return null;
                    }
                }.execute();
            } else if (data.getCount() > 0)
                showReviews();
            reviewAdapter.setmId(Integer.parseInt(movieId));
            reviewAdapter.swapCursor(data);
            if (mReviewPosition == RecyclerView.NO_POSITION) mReviewPosition = 0;
            
            if (reviewRecycleView != null)
                reviewRecycleView.smoothScrollToPosition(mReviewPosition);
            
            
        }
        
        
    }
    
    private void showTrailerMessage() {
        trailersRecycleView.setVisibility(View.GONE);
        noTrailer.setVisibility(View.VISIBLE);
    }
    
    public void showTrailers() {
        trailersRecycleView.setVisibility(View.VISIBLE);
        noTrailer.setVisibility(View.INVISIBLE);
    }
    
    
    private void showReviewDisplayMessage() {
        reviewRecycleView.setVisibility(View.GONE);
        noReview.setVisibility(View.VISIBLE);
    }
    
    public void showReviews() {
        reviewRecycleView.setVisibility(View.VISIBLE);
        noReview.setVisibility(View.INVISIBLE);
    }
    
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        trailerAdapter.swapCursor(null);
        reviewAdapter.swapCursor(null);
    }
}
