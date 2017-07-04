package com.example.ahsan.popularmovies.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.example.ahsan.popularmovies.model.details.Reviews;
import com.example.ahsan.popularmovies.model.details.Videos;
import com.example.ahsan.popularmovies.webservices.RemoteMoviesAPI;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.ahsan.popularmovies.Utilities.MoviePreferences.DEFAULT_BACKDROP_SIZE;
import static com.example.ahsan.popularmovies.enums.MovieResponse.POSTERPATH;

public class MovieDetails extends Fragment implements OnBackClickListener {
    private static final int ID_MOVIE_REVIEWS = 6000;
    private static final int ID_MOVIE_TRAILERS = 5000;
    private static final int ID_MOVIE = 7000;
    private static final int ID_MOVIE_ALL = 8000;
    private static MovieDetails sInstance;
    // TODO: Rename parameter arguments, choose names that match
    
    String title = "";
    String overview = "";
    String rating = "0";
    String release = "";
    String thumbnail = null;
    String durationTime;
    RecyclerView trailersRecycleView;
    RecyclerView reviewRecycleView;
    private boolean isFavorite = false;
    private String movieId;
    private TrailerAdapter trailerAdapter;
     private TextView noReview;
    private TextView noTrailer;
    private TextView duration;
    
    private BackButtonHandlerInterface backButtonHandler;
    private ReviewAdapter reviewAdapter;
     private OnFragmentInteractionListener mListener;
    private int movieType;
     
    
    // TODO: Rename and change types and number of parameters
    public static MovieDetails newInstance(Bundle bundle) {
        Bundle args = new Bundle();
         if (sInstance == null) {
            sInstance = new MovieDetails();
            args.putAll(bundle);
            sInstance.setArguments(args);
        }
          return sInstance;
    }
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        backButtonHandler = (BackButtonHandlerInterface) context;
        backButtonHandler.addBackClickListener(this);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

 
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras;
        
        
        if (savedInstanceState != null) {
            extras = savedInstanceState;
        } else {
            extras = this.getArguments();
        }
        if (extras != null) {
            
            movieType = extras.getInt(MovieContract.MovieBase.COLUMN_TABLE_NAME);
            title = extras.getString(MovieResponse.TITLE.name());
            overview = extras.getString(MovieResponse.OVERVIEW.name());
            rating = extras.getString(MovieResponse.RATING.name());
            thumbnail = extras.getString(POSTERPATH.name());
            release = extras.getString(MovieResponse.RELEASE_DATE.name());
            isFavorite = extras.getInt(MovieResponse.FAVORITE.name()) == 1;
            movieId = extras.getString(MovieResponse.MOVIEID.name());
            String releaseYear = getReleaseYear(release);
             release = releaseYear;
        }
        trailerAdapter = new TrailerAdapter(getContext(), Integer.parseInt(movieId));
        reviewAdapter = new ReviewAdapter(getContext(), Integer.parseInt(movieId));
        
    }
    
    private String getReleaseYear(String release) {
        if (release.length() == 4)
            return release;
        
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
            int counter = 0;
            
            @Override
            public void onClick(View v) {
                isFavorite = !isFavorite;
                ContentValues values = new ContentValues();
                Logger.d("PRINTOUT ON CLICK!!-> COUNTER: " + counter++);
                values.put(MovieContract.MovieBase.COLUMN_FAVORITES, isFavorite == true ? 1 : 0);
                values.put("MOVIE_TYPE", movieType);
                getActivity().getContentResolver().update(MovieContract.FAVORITES_URI, values, "movieid = ?", new String[]{movieId});


                
                if (isFavorite) {
                    favorites.setBackground(getResources().getDrawable(R.drawable.ic_favorite_white_24dp));
                    Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();
                } else {
                    favorites.setBackground(getResources().getDrawable(R.drawable.ic_favorite_border_white_24dp));
                    Toast.makeText(getContext(), "Unsaved", Toast.LENGTH_SHORT).show();
                }
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
        
        HashMap<String, String> map = new HashMap<>();
        map.put("append_to_response", "reviews,videos");
        RemoteMoviesAPI.getInstance().getMovie(String.valueOf(movieId), map).enqueue(new Callback<com.example.ahsan.popularmovies.model.details.MovieDetails>() {
            @Override
            public void onResponse(Call<com.example.ahsan.popularmovies.model.details.MovieDetails> call, Response<com.example.ahsan.popularmovies.model.details.MovieDetails> response) {
                
                if (response != null && response.body() != null) {
                    Videos trailerVideos = response.body().getVideos();
                    Reviews movieReviews = response.body().getReviews();
                    if (trailerVideos.getResults().size() > 0) {
                        showTrailers();
                        trailerAdapter.setData(trailerVideos);
                        trailersRecycleView.setAdapter(trailerAdapter);
                    } else {
                        
                        showTrailerMessage();
                    }
                    
                    if (movieReviews.getReviewResults().size() > 0) {
                        showReviews();
                        reviewAdapter.setData(movieReviews);
                        reviewRecycleView.setAdapter(reviewAdapter);
                    } else {
                        showReviewDisplayMessage();
                        
                    }
                    durationTime = String.valueOf(response.body().getRuntime());
                    duration.setText(durationTime + " min");
                }
            }
            
            @Override
            public void onFailure(Call<com.example.ahsan.popularmovies.model.details.MovieDetails> call, Throwable t) {
                
                Logger.d(t.getMessage());
                new Throwable(t);
            }
            
        });
        
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        
        if (outState != null) {
            outState.putString(MovieResponse.TITLE.name(), title);
            outState.putString(MovieResponse.OVERVIEW.name(), overview);
            outState.putString(MovieResponse.RATING.name(), rating);
            outState.putString(MovieResponse.THUMBNAIL.name(), thumbnail);
            outState.putString(MovieResponse.RELEASE_DATE.name(), release);
            outState.putInt(MovieResponse.FAVORITE.name(), isFavorite == true ? 1 : 0);
            outState.putString(MovieResponse.MOVIEID.name(), movieId);
            outState.putString(MovieResponse.DURATION.name(), durationTime);
            outState.putInt(MovieContract.MovieBase.COLUMN_TABLE_NAME, movieType);
        }
    }
    
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        backButtonHandler.removeBackClickListener(this);
        backButtonHandler = null;
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
    public boolean onBackClick() {
        if (mListener != null) {
            mListener.onFragmentInteraction(null);
        }
        return true;
    }
    
    
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Bundle bundle);
        
    }
}
