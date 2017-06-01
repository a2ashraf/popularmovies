package com.example.ahsan.popularmovies.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.ahsan.popularmovies.R;
import com.example.ahsan.popularmovies.adapters.RAdapter;
import com.example.ahsan.popularmovies.data.MovieContract;
import com.example.ahsan.popularmovies.model.Images;
import com.example.ahsan.popularmovies.model.Result;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;

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
public class MovieListing extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    
    public final static int MOVIE_TYPE_TOP_RATED = 1;
    public final static int MOVIE_TYPE_POPULAR = 0;
    public final static int MOVIE_TYPE_FAVORITES = 2;
    
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int ID_MOVIES_POPULAR = 1000;
    private static final int ID_MOVIES_TOPRATED = 2000;
    private static final int ID_MOVIES_FAVORITES = 3000;
    private static MovieListing sInstance;
    public String imageBaseURL = "";
    protected RecyclerView recyclerView;
    Bundle stateSaver;
    String sort_by;
    private RAdapter myAdapter;
    private JSONArray arrayOfMovies;
    private ArrayList<Result> movies;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private boolean valid = false;
    private OnFragmentInteractionListener mListener;
    private View returnView;
    private int movieType;
    private int adapterPosition = RecyclerView.NO_POSITION;
    private int COLOR_BLACK;
    private int COLOR_GREY;
    private boolean forceLoad;
    
    public MovieListing() {
        // Required empty public constructor
    }
    
    
    // TODO: Rename and change types and number of parameters
    public static MovieListing newInstance(int movieType) {
        d(" ");
        if (sInstance == null) {
            sInstance = new MovieListing();
            switch (movieType) {
                case MOVIE_TYPE_POPULAR:
                    sInstance.setMovieType(MOVIE_TYPE_POPULAR);
                    return sInstance;
                case MOVIE_TYPE_TOP_RATED:
                    sInstance.setMovieType(MOVIE_TYPE_TOP_RATED);
                    return sInstance;
                case MOVIE_TYPE_FAVORITES:
                    sInstance.setMovieType(MOVIE_TYPE_FAVORITES);
                    return sInstance;
            }
        }
        return sInstance;
    }
    
    //send to base
    public void onButtonPressed(Bundle bundle) {
        d("Pressing the button");
        if (mListener != null) {
            mListener.onFragmentInteraction(bundle);
        }
    }
    
    //send to base
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
        super.onCreate(savedInstanceState);
        stateSaver = savedInstanceState;
        setMovieType(MOVIE_TYPE_TOP_RATED);
//        if (stateSaver != null) {
//            if (stateSaver.getSerializable("movie_type") != null) {
//                movieType = (int) stateSaver.getSerializable("movie_type");
//                d("sort_by obtained from serialized state ");
//            } else {
//                setMovieType(MOVIE_TYPE_POPULAR);
//                d("Default move search order if not saved");
//            }
//        } else {
//            setMovieType(MOVIE_TYPE_TOP_RATED);
//
//            d("stateSaver was null");
//        }
        makeRequest(false);
        
    }
    
    
    //    public static void setConfigOptions(Images options){}
    //send to base?
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        d(" ");
        setHasOptionsMenu(true);
        
        
        returnView = inflater.inflate(R.layout.fragment_movie_listing, container, false);
        COLOR_BLACK = getResources().getColor(R.color.colorBlack);
        COLOR_GREY = getResources().getColor(R.color.colorGray);
        try {
            setData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
 
        
        
        return returnView;
    }

 
    
    //base?
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    
    //base
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Logger.t(5).d("Should only be once right? MENU");
        menu.clear();
        inflater.inflate(R.menu.sort, menu);
        //-> this is where we can save one menu and then toggle visibility if needed on other menu items to guide choices.
        
        //   super.onCreateOptionsMenu(menu, inflater);
    }
    
    //base
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        forceLoad=true;
         //noinspection SimplifiableIfStatement
        if (id == R.id.action_top_rated) {
            //swap fragments by calling home activity to do the work
            setMovieType(MOVIE_TYPE_TOP_RATED);
            makeRequest(forceLoad);
    
            // item.setVisible(false);
         //   item.
            return true;
        }
        
        if (id == R.id.action_popularity) {
            //swap fragments by calling home activity to do the work
            setMovieType(MOVIE_TYPE_POPULAR);
            makeRequest(forceLoad);
           // item.setVisible(false);
            return true;
        }
//
//
        if (id == R.id.action_favorites) {
            //swap fragments by calling home activity to do the work
            setMovieType(MOVIE_TYPE_FAVORITES);
            makeRequest(forceLoad);
        }
        return false;
    }
    
    //maybe abstract it.
    public void setData() throws JSONException {
        
        recyclerView = (RecyclerView) returnView.findViewById(R.id.movie_recycler_view);
        recyclerView.setBackgroundColor(COLOR_GREY);
        recyclerView.setHasFixedSize(true);
        returnView.setBackgroundColor(COLOR_BLACK);
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
         recyclerView.setLayoutManager(mLayoutManager);
        myAdapter = new RAdapter(this.getContext(),this);
        recyclerView.setAdapter(myAdapter);
        
    }
    
    @Override
    public void onResume() {
        
        super.onResume();
//        switch (getMovieType()) {
//            case MOVIE_TYPE_POPULAR:
//                getActivity().setTitle(R.string.sort_popular);
//                break;
//            case MOVIE_TYPE_TOP_RATED:
//                getActivity().setTitle(R.string.sort_rated);
//                break;
//            case MOVIE_TYPE_FAVORITES:
//                getActivity().setTitle(R.string.favorites);
//                break;
//        }
        
    }

//
//        RemoteMoviesAPI.getInstance().getReviews("321612",null).enqueue(new Callback<Reviews>() {
//            @Override
//            public void onResponse(Call<Reviews> call, Response<Reviews> response) {
//                Logger.d(response.body().toString());
//
//            }
//
//            @Override
//            public void onFailure(Call<Reviews> call, Throwable t) {
//                Logger.d(t.getMessage());
//                new Throwable(t);
//            }
//        });
//
//
//
//        RemoteMoviesAPI.getInstance().getVideos("321612",null).enqueue(new Callback<Videos>() {
//            @Override
//            public void onResponse(Call<Videos> call, Response<Videos> response) {
//                Logger.d(response.body().toString());
//
//            }
//
//            @Override
//            public void onFailure(Call<Videos> call, Throwable t) {
//                Logger.d(t.getMessage());
//                new Throwable(t);
//            }
//        });
//
//
    
    public void makeRequest(boolean forceLoad) {
        
        
        switch (getMovieType()) {
            case MOVIE_TYPE_POPULAR:
                getActivity().setTitle(R.string.sort_popular);
                if (getActivity().getSupportLoaderManager().getLoader(ID_MOVIES_POPULAR) == null || forceLoad)
                    getActivity().getSupportLoaderManager().initLoader(ID_MOVIES_POPULAR, null, this);
                break;
            case MOVIE_TYPE_TOP_RATED:
                getActivity().setTitle(R.string.sort_rated);
                if (getActivity().getSupportLoaderManager().getLoader(ID_MOVIES_TOPRATED) == null || forceLoad)
                    getActivity().getSupportLoaderManager().initLoader(ID_MOVIES_TOPRATED, null, this);
                
                break;
            case MOVIE_TYPE_FAVORITES:
                getActivity().setTitle(R.string.favorites);
                if (getActivity().getSupportLoaderManager().getLoader(ID_MOVIES_FAVORITES) == null|| forceLoad )
                    getActivity().getSupportLoaderManager().initLoader(ID_MOVIES_FAVORITES, null, this);
        
                break;
        }
        
        
    }
    
    private int getMovieType() {
        return movieType;
    }
    
    private void setMovieType(int movieTypeTopRated) {
        movieType = movieTypeTopRated;
    }
    
    public String getImageBaseURL() {
        return imageBaseURL;
    }
    
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Uri uri = null;
        switch (id) {
            case (ID_MOVIES_POPULAR):
                //return a cursor to this URI
                uri = MovieContract.MoviePopular.CONTENT_URI;
                break;
            case (ID_MOVIES_TOPRATED):
                uri = MovieContract.MovieTopRated.CONTENT_URI;
                break;
            case (ID_MOVIES_FAVORITES):
                uri = MovieContract.FAVORITES_URI;
                break;
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
            
            
        }
        CursorLoader loader = new CursorLoader(getContext(), uri, null, null, null, null);
        return loader;
        
    }
    
    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        
        
        if (adapterPosition == RecyclerView.NO_POSITION)
            adapterPosition = 0;
         myAdapter.swapCursor(data);
        
        if (data.getCount() != 0) {
            
            try {
                recyclerView.smoothScrollToPosition(adapterPosition);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void onLoaderReset(Loader loader) {
        myAdapter.swapCursor(null);
    }
    
  
    
    
    /* This interface must be implemented by activities that contain this
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
        Images getImageOption();
    
    }
    
    
 
}
