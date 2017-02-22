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

import com.example.ahsan.popularmovies.R;
import com.example.ahsan.popularmovies.enums.MovieResponse;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

public class MovieDetails extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    String title = "";
    String overview = "";
    String rating = "0";
    String release = "";
    String thumbnail = null;

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
        View detailsView = inflater.inflate(R.layout.details, container, false);
        Bundle extras;
        if (savedInstanceState != null)
            extras = savedInstanceState;
        else
            extras = this.getArguments();


        if (extras != null) {
            title = extras.getString(MovieResponse.TITLE.name());
            overview = extras.getString(MovieResponse.OVERVIEW.name());
            rating = extras.getString(MovieResponse.RATING.name());
            thumbnail = extras.getString(MovieResponse.THUMBNAIL.name());
            release = extras.getString(MovieResponse.RELEASE_DATE.name());
        }

        TextView title_view = (TextView) detailsView.findViewById(R.id.title_of_movie);
        title_view.setText(title.toString());
        TextView overview_view = (TextView) detailsView.findViewById(R.id.overview);
        overview_view.setText(overview.toString());

        TextView rating_view = (TextView) detailsView.findViewById(R.id.rating);
        rating_view.setText(rating_view.getText().toString() + "  " + rating);

        TextView release_view = (TextView) detailsView.findViewById(R.id.release_date);
        release_view.setText(release_view.getText() + "  " + release.toString());

        final ImageView background_view = (ImageView) detailsView.findViewById(R.id.background_image);
        final String finalThumbnail = thumbnail;

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
    }



}
