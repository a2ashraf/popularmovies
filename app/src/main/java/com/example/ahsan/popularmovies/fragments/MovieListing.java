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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.ahsan.popularmovies.R.menu.sort;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MovieListing.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MovieListing#newInstance} factory method to
 * create an instance of this fragment.
 */
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

    public MovieListing() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static MovieListing newInstance(Bundle bundle) {
        MovieListing fragment = new MovieListing();
        Bundle args = new Bundle();
        if(bundle!=null){
            args.putString(ARG_PARAM1, "details");
            args.putAll(bundle);

        }else{

            args.putString(ARG_PARAM1, "listing");


        }

        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(sort, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        FetchData movieDataService = new FetchData(this);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_top_rated) {

            movieDataService.execute("0", SORTBY_TOP_RATED);
            sort_by = SORTBY_TOP_RATED;
            return true;
        }

        if (id == R.id.action_popularity) {
            movieDataService.execute("0", SORTBY_POPULAR);
            sort_by = SORTBY_POPULAR;
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
         }

        sort_by = SORTBY_TOP_RATED;
        stateSaver = savedInstanceState;

        if ((savedInstanceState != null)
                && (savedInstanceState.getSerializable("sort_by") != null)) {
            sort_by = (String) savedInstanceState
                    .getSerializable("sort_by");
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View returnView ;
//        if(mParam1.equals("details")){
//            returnView = inflater.inflate(R.layout.details, container, false);
//            return returnView;
//        }else {
            returnView = inflater.inflate(R.layout.fragment_movie_listing, container, false);
            recyclerView = (RecyclerView) returnView.findViewById(R.id.movie_recycler_view);
            recyclerView.setHasFixedSize(true);
            StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(mLayoutManager);
            return returnView;
//        }




    }

    public void setData(JSONArray result) throws JSONException {
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
        myAdapter = new RAdapter(this, movies);
        recyclerView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();

    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Bundle bundle) {
        if (mListener != null) {
            mListener.onFragmentInteraction(bundle);
        }
    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
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
    public void onPause() {
        if (stateSaver != null)
            stateSaver.putSerializable("sort_by", sort_by);

        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {

        super.onResume();
        FetchConfiguration configuration = new FetchConfiguration(this);

        if (imageBaseURL.equals("") && isOnline()) {
            configuration.execute();
        }

        FetchData movieDataService = new FetchData(this);

        if (isOnline()) {
            movieDataService.execute("0", sort_by);
        } else
            Toast.makeText(getActivity(), "Please verify your internet connection, and try again", Toast.LENGTH_SHORT).show();


    }

    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putSerializable("sort_by", sort_by);
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
