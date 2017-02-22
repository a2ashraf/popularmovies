package com.example.ahsan.popularmovies.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ahsan.popularmovies.R;
import com.example.ahsan.popularmovies.adapters.RAdapter;
import com.example.ahsan.popularmovies.enums.MovieResponse;
import com.example.ahsan.popularmovies.model.Movie;
import com.example.ahsan.popularmovies.webservices.FetchConfiguration;
import com.example.ahsan.popularmovies.webservices.FetchData;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.orhanobut.logger.Logger.d;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MovieListing.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MovieListing#newInstance} factory method to
 * create an instance of this fragment.
 */

//TODO: why is it being called twice? fetch data!
public class MovieListing extends Fragment {
    public static final String SORTBY_TOP_RATED = "SORTBY_TOP_RATED";
    public static final String SORTBY_POPULAR = "SORTBY_POPULAR";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public String imageBaseURL = "";
    Bundle stateSaver;
    String sort_by;
    private RAdapter myAdapter;
    private JSONArray arrayOfMovies;
    private RecyclerView recyclerView;
    private ArrayList<Movie> movies;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private boolean valid = false;
    private OnFragmentInteractionListener mListener;
    private View returnView;

    public MovieListing() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static MovieListing newInstance(Bundle bundle) {
        d(" ");
        MovieListing fragment = new MovieListing();
        return fragment;
    }

    public void setData(JSONArray result) throws JSONException {
        d(" ");
        arrayOfMovies = result;
        movies = new ArrayList<>();
        movies.clear();
        for (int i = 0; i < arrayOfMovies.length(); i++) {
            JSONObject aMovie = arrayOfMovies.getJSONObject(i);
            Movie movie = new Movie();
            movie.setTitle(aMovie.getString(MovieResponse.TITLE.value));
            movie.setImageBackground(aMovie.getString(MovieResponse.THUMBNAIL.value));
            movie.setOverview(aMovie.getString(MovieResponse.OVERVIEW.value));
            movie.setReleaseDate(aMovie.getString(MovieResponse.RELEASE_DATE.value));
            movie.setUserRating(Float.parseFloat(aMovie.getString(MovieResponse.RATING.value)));
            movies.add(movie);
        }

        recyclerView = (RecyclerView) returnView.findViewById(R.id.movie_recycler_view);
        recyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        myAdapter = new RAdapter(this, movies);
        recyclerView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
    }

    public void onButtonPressed(Bundle bundle) {
        d("Pressing the button");
        if (mListener != null) {
            mListener.onFragmentInteraction(bundle);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        d(" ");
        super.onCreate(savedInstanceState);
        stateSaver = savedInstanceState;
        if (stateSaver != null) {
            if (stateSaver.getSerializable("sort_by") != null) {
                sort_by = (String) stateSaver.getSerializable("sort_by");
                d("sort_by obtained from serialized state ");
            } else {
                sort_by = SORTBY_TOP_RATED;
                d("EVER COME HERE? ");
            }
        } else {
            sort_by = SORTBY_TOP_RATED;
            d("stateSaver was null");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        d(" ");
        setHasOptionsMenu(true);
        returnView = inflater.inflate(R.layout.fragment_movie_listing, container, false);
        return returnView;
    }

    @Override
    public void onResume() {
        d(" ");
        super.onResume();
//        if (stateSaver != null) {
//            sort_by = (String) stateSaver.getSerializable("sort_by");
//        }
        makeRequest();
    }

    public void makeRequest() {
        FetchConfiguration configuration = new FetchConfiguration(this);
        if (imageBaseURL.equals("") && isOnline()) {
            configuration.execute();
        }

        FetchData movieDataService = new FetchData(this);
        if (isOnline()) {
            d("Making service request");
            movieDataService.execute(sort_by);
        } else
            Toast.makeText(getActivity(), "Please verify your internet connection, and try again", Toast.LENGTH_SHORT).show();

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        Logger.t(10).d("Saving State sort_by  " + sort_by);
        state.putSerializable("sort_by", sort_by);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (stateSaver != null) {
            stateSaver.putSerializable("sort_by", sort_by);
            Logger.d(stateSaver.getSerializable("sort_by").toString());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Logger.t(5).d("Should only be once right? MENU");
        menu.clear();
        inflater.inflate(R.menu.sort, menu);

        //   super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        d("Only once, on inital");
        int id = item.getItemId();
        FetchData movieDataService = new FetchData(this);
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_top_rated) {

            movieDataService.execute(SORTBY_TOP_RATED);
            sort_by = SORTBY_TOP_RATED;
            return true;
        }

        if (id == R.id.action_popularity) {
            movieDataService.execute(SORTBY_POPULAR);
            sort_by = SORTBY_POPULAR;
            return true;
        }
        return false;
    }

    public String getImageBaseURL() {
        return imageBaseURL;
    }

    public void setImageBaseURL(String imageBaseURL) {
        this.imageBaseURL = imageBaseURL;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Bundle bundle);
    }
}
