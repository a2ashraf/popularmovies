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
import android.widget.Toast;

import com.example.ahsan.popularmovies.R;
import com.example.ahsan.popularmovies.adapters.RAdapter;
import com.example.ahsan.popularmovies.data.MovieContract;
import com.example.ahsan.popularmovies.model.Images;
import com.example.ahsan.popularmovies.model.MovieResult;
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

 public class MovieListing extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    
    public final static int MOVIE_TYPE_TOP_RATED = 1;
    public final static int MOVIE_TYPE_POPULAR = 0;
    public final static int MOVIE_TYPE_FAVORITES = 2;
    
    
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
    private ArrayList<MovieResult> movies;
     private String mParam1;
    private String mParam2;
    private boolean valid = false;
    private OnFragmentInteractionListener mListener;
    private View returnView;
    private int movieType;
    private static int tableSource;
    private int adapterPosition = RecyclerView.NO_POSITION;
    private int COLOR_BLACK;
    private int COLOR_GREY;
    private boolean forceLoad;
    
    public MovieListing() {
        // Required empty public constructor
    }
    
    
     public static MovieListing newInstance(int movieType) {
        d(" ");
        if (sInstance == null) {
            sInstance = new MovieListing();
            switch (movieType) {
                case MOVIE_TYPE_POPULAR:
                    tableSource =    MOVIE_TYPE_POPULAR;
                    sInstance.setMovieType(MOVIE_TYPE_POPULAR);
                    return sInstance;
                case MOVIE_TYPE_TOP_RATED:
                    tableSource =    MOVIE_TYPE_TOP_RATED;
    
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
            bundle.putInt("MOVIE_TYPE",sInstance.tableSource);
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
 
        makeRequest(false);
        
    }
    
    
    //    public static void setConfigOptions(Images options){}
    //send to base?
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        d("loadFlow: onCreateView MovieListing");
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
     }
    
     @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        forceLoad=true;
         if (id == R.id.action_top_rated) {
             tableSource =    MOVIE_TYPE_TOP_RATED;
    
             setMovieType(MOVIE_TYPE_TOP_RATED);
            makeRequest(forceLoad);
            return true;
        }
        
        if (id == R.id.action_popularity) {
            tableSource =    MOVIE_TYPE_POPULAR;

             setMovieType(MOVIE_TYPE_POPULAR);
            makeRequest(forceLoad);
             return true;
        }
 
        if (id == R.id.action_favorites) {
            setMovieType(MOVIE_TYPE_FAVORITES);
            makeRequest(forceLoad);
        }
        return false;
    }
    
     public void setData() throws JSONException {
        
        recyclerView = (RecyclerView) returnView.findViewById(R.id.movie_recycler_view);
        recyclerView.setHasFixedSize(false);
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        myAdapter = new RAdapter(this.getContext().getApplicationContext(),this);
        recyclerView.setAdapter(myAdapter);
    }
    
    
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
//                String selection = " = ?";
//                String[] selectionargs = new String[]{""};
                break;
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
            
            
        }
        CursorLoader loader = new CursorLoader(getContext(), uri, null, null, null, null);
        
        return loader;
        
    }
    
    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        if(data.getCount()==0 && data.getNotificationUri().getLastPathSegment().equals("favorites") && getActivity()!=null){
            Toast.makeText(getActivity(), "NO MOVIES TO DISPLAY\n Please favorite a movie by clicking the Heart", Toast.LENGTH_LONG).show();
        }
        
        if (adapterPosition == RecyclerView.NO_POSITION)
            adapterPosition = 0;
         myAdapter.swapCursor(data);
        
        if (null!=data && data.getCount() != 0) {
            
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
   
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Bundle bundle);
        Images getImageOption();
    
    }
    
    
 
}
