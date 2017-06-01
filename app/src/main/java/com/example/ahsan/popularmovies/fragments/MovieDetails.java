package com.example.ahsan.popularmovies.fragments;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahsan.popularmovies.R;
import com.example.ahsan.popularmovies.Utilities.MoviePreferences;
import com.example.ahsan.popularmovies.enums.MovieResponse;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import static com.example.ahsan.popularmovies.Utilities.MoviePreferences.DEFAULT_BACKDROP_SIZE;

public class MovieDetails extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    
    String title = "";
    String overview = "";
    String rating = "0";
    String release = "";
    String thumbnail = null;
    private boolean isFavorite;
    private String movieId;
    
    // TODO: Rename and change types and number of parameters
    public static MovieDetails newInstance(Bundle bundle) {
        MovieDetails fragment = new MovieDetails();
        
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
        
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        setHasOptionsMenu(false);
        View detailsView = inflater.inflate(R.layout.fragment_movie_details, container, false);
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
            
        }
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
                    MoviePreferences.setToFavorites(getContext(), movieId);
                } else {
                    favorites.setBackground(getResources().getDrawable(R.drawable.ic_favorite_border_white_24dp));
                    MoviePreferences.removeFromFavorites(getContext(), movieId);
                    
                    Toast.makeText(getContext(), "Unsaved", Toast.LENGTH_SHORT).show();
                }
                isFavorite = !isFavorite;
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
//        final String finalThumbnail = MoviePreferences.getBaseUrl(getContext()) + MoviePreferences.getBackDropSize(getContext()) + thumbnail;
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
        
        
        return detailsView;
    }
    
    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        Logger.d("Saving State sort_by");
        state.putSerializable(MovieResponse.TITLE.name(), title);
        state.putSerializable(MovieResponse.OVERVIEW.name(), overview);
        state.putSerializable(MovieResponse.RATING.name(), rating);
        state.putSerializable(MovieResponse.THUMBNAIL.name(), thumbnail);
        state.putSerializable(MovieResponse.RELEASE_DATE.name(), release);
        state.putSerializable(MovieResponse.FAVORITE.name(), isFavorite);
        state.putSerializable(MovieResponse.MOVIEID.name(), movieId);
        
    }
    
    
}
