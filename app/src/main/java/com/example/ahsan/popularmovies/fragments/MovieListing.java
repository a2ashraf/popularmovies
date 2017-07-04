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
import com.example.ahsan.popularmovies.sync.MovieSyncTask;
import com.orhanobut.logger.Logger;

import org.json.JSONException;

import java.util.ArrayList;

import static com.example.ahsan.popularmovies.data.MovieContract.MovieBase.COLUMN_MOVIEID;
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
    
    
    private static final int ID_MOVIES = 1000;
    
    private static MovieListing sInstance;
     public String imageBaseURL = "";
    protected RecyclerView recyclerView;
    Bundle stateSaver;
     private RAdapter myAdapter;
    private OnFragmentInteractionListener mListener;
    private View returnView;
    private int movieType;
    private int adapterPosition = RecyclerView.NO_POSITION;
    private int COLOR_BLACK;
    private int COLOR_GREY;
    private boolean forceLoad;
    private Cursor data;
 
    
    public MovieListing() {
     }
    
    public static MovieListing newInstance() {
        d(" ");
        if (sInstance == null) {
            sInstance = new MovieListing();
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
        if (stateSaver != null) {
            int layout_type = stateSaver.getInt("SCREENTYPE");
            setMovieType(layout_type);
         } else
            setMovieType(MOVIE_TYPE_TOP_RATED);
        
        
        if (getActivity().getSupportLoaderManager().getLoader(ID_MOVIES) == null || forceLoad) {
            getActivity().getSupportLoaderManager().initLoader(ID_MOVIES, null, this);
        }
        
    }
    
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
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        
        
        if (outState != null) {
             outState.putInt("SCREENTYPE", getMovieType());
        }
    }
    
    private int getMovieType() {
        return movieType;
    }
    
    private void setMovieType(int movieTypeTopRated) {
        
        movieType = movieTypeTopRated;
        
        switch (movieType) {
            case MOVIE_TYPE_POPULAR:
                getActivity().setTitle(getActivity().getResources().getString(R.string.sort_popular));
                break;
            case MOVIE_TYPE_TOP_RATED:
                getActivity().setTitle(getActivity().getResources().getString(R.string.sort_rated));
                break;
            case MOVIE_TYPE_FAVORITES:
                getActivity().setTitle(getActivity().getResources().getString(R.string.favorites));
                break;
        }
        
        
    }
    
    @Override
    public void onPause() {
        super.onPause();
        
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
        forceLoad = true;
        if (id == R.id.action_top_rated) {
            setMovieType(MOVIE_TYPE_TOP_RATED);
            getActivity().setTitle(getResources().getString(R.string.sort_rated));
        }
        
        if (id == R.id.action_popularity) {
            setMovieType(MOVIE_TYPE_POPULAR);
            getActivity().setTitle(getResources().getString(R.string.sort_popular));
        }
        
        if (id == R.id.action_favorites) {
            setMovieType(MOVIE_TYPE_FAVORITES);
            getActivity().setTitle(getResources().getString(R.string.favorites));
        }
        makeRequest(false);
        return true;
    }
    
    public void setData() throws JSONException {
        recyclerView = (RecyclerView) returnView.findViewById(R.id.movie_recycler_view);
        recyclerView.setHasFixedSize(false);
        StaggeredGridLayoutManager amLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(amLayoutManager);
        myAdapter = new RAdapter(this.getContext().getApplicationContext(), this);
        recyclerView.setAdapter(myAdapter);
    }
    
    public String getImageBaseURL() {
        return imageBaseURL;
    }
    
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if (ID_MOVIES == id) {
            Uri uri = MovieContract.MovieBase.CONTENT_URI;
            CursorLoader loader = new CursorLoader(getContext(), uri, null, null, null, null);
            return loader;
        }
        
        return null;
    }
    
    @Override
    public void onLoadFinished(Loader loader, Cursor currentCursor) {
        if (adapterPosition == RecyclerView.NO_POSITION)
            adapterPosition = 0;
        
        setCursorData(currentCursor);
        try {
            myAdapter.swapCursor(getAllMovies(getMovieType()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        recyclerView.smoothScrollToPosition(adapterPosition);
    }
    
    private void setCursorData(Cursor currentCursor) {
        data = currentCursor;
    }
    
    @Override
    public void onLoaderReset(Loader loader) {
        try {
            myAdapter.swapCursor(getAllMovies(getMovieType()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(ID_MOVIES, null, this);
    }
    
    public void makeRequest(boolean forceLoad) {
        ArrayList<RAdapter.AMovieOFDetails> allMovies = null;
        try {
            allMovies = getAllMovies(getMovieType());
            if (allMovies.size() == 0) {
                Toast.makeText(getActivity(), "NO MOVIES TO DISPLAY\n Please favorite a movie by clicking the Heart", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (myAdapter != null && allMovies != null) {
            myAdapter.swapCursor(allMovies);
        }
        
    }
    
    //this should take cursor and go through it once giving you all movies of a certain type: yes to favorites or yes to a type. add fav to args and search by that.
    private ArrayList<RAdapter.AMovieOFDetails> getAllMovies(int type) throws Exception {
        Cursor currentCursor = null;
        ArrayList<RAdapter.AMovieOFDetails> allMovies = new ArrayList<>();
        if (data == null)
            return null;
        currentCursor = data;
        
        
        switch (getMovieType()) {
            case (MOVIE_TYPE_TOP_RATED):
                
                
                for (int i = 0; i < currentCursor.getCount(); i++) {
                    currentCursor.moveToPosition(i);
                    int sourceTableNameindex = currentCursor.getColumnIndex(MovieContract.MovieBase.COLUMN_TABLE_NAME);
                    final int sourceDataType = currentCursor.getInt(sourceTableNameindex);
                    if (sourceDataType == (getMovieType() == MOVIE_TYPE_TOP_RATED ? MovieSyncTask.TOP_RATED : MovieSyncTask.POPULAR)) {
                        allMovies.add(getAMovieDetail(currentCursor));
                    }
                    
                }
                break;
            case (MOVIE_TYPE_POPULAR):
                
                
                for (int i = 0; i < currentCursor.getCount(); i++) {
                    currentCursor.moveToPosition(i);
                    int sourceTableNameindex = currentCursor.getColumnIndex(MovieContract.MovieBase.COLUMN_TABLE_NAME);
                    final int sourceDataType = currentCursor.getInt(sourceTableNameindex);
                    if (sourceDataType == (getMovieType() == MOVIE_TYPE_TOP_RATED ? MovieSyncTask.TOP_RATED : MovieSyncTask.POPULAR)) {
                        allMovies.add(getAMovieDetail(currentCursor));
                        
                    }
                    
                }
                break;
            case (MOVIE_TYPE_FAVORITES):
                
                
                for (int i = 0; i < currentCursor.getCount(); i++) {
                    currentCursor.moveToPosition(i);
                    int sourceTableNameindex = currentCursor.getColumnIndex(MovieContract.MovieBase.COLUMN_FAVORITES);
                    final int sourceisFavorite = currentCursor.getInt(sourceTableNameindex);
                    if (sourceisFavorite != 0) {
                        allMovies.add(getAMovieDetail(currentCursor));
                        
                    }
                    
                }
                break;
            default:
                throw new Exception();
        }
        
        
        return allMovies;
    }
    
    private RAdapter.AMovieOFDetails getAMovieDetail(Cursor currentCursor) {
        int imageColumnIndex = currentCursor.getColumnIndex(MovieContract.MovieBase.COLUMN_POSTERPATH);
        final String ImageURL = currentCursor.getString(imageColumnIndex);
        RAdapter.AMovieOFDetails md = new RAdapter.AMovieOFDetails();
        md.setId(Integer.valueOf(currentCursor.getString(currentCursor.getColumnIndex(COLUMN_MOVIEID))));
        md.setVoteAverage(Double.parseDouble(String.valueOf(currentCursor.getColumnIndex(MovieContract.MovieBase.COLUMN_VOTEAVERAGE))));
        
        md.setReleaseDate(currentCursor.getString(currentCursor.getColumnIndex(MovieContract.MovieBase.COLUMN_RELEASEDATE)));
        md.setOverview(currentCursor.getString(currentCursor.getColumnIndex(MovieContract.MovieBase.COLUMN_OVERVIEW)));
        md.setOriginalTitle(currentCursor.getString(currentCursor.getColumnIndex(MovieContract.MovieBase.COLUMN_ORIGINALTITLE)));
        md.setFavorite(currentCursor.getInt(currentCursor.getColumnIndex(MovieContract.MovieBase.COLUMN_FAVORITES)));
        md.setMovieType(currentCursor.getInt(currentCursor.getColumnIndex(MovieContract.MovieBase.COLUMN_TABLE_NAME)));
        
        md.setPosterPath(ImageURL);
        return md;
    }
    
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Bundle bundle);
        
        Images getImageOption();
        
    }
    
    
}
