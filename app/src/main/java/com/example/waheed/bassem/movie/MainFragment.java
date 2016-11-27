package com.example.waheed.bassem.movie;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * MainFragment
 * this will contain the main grid view that displays the movies as the user wants
 */

public class MainFragment extends Fragment implements AsyncTaskCallBack {

    private GridView mMainGrid;
    private ArrayList<Movie> mMovieList;
    private MovieAdapter mMovieAdapter;
    private TextView mLoadingTextView;
    private ProgressBar mLoadingProgressBar;
    private Spinner mSortingSpinner;
    private ArrayAdapter<CharSequence> mSortingArrayAdapter;
    private SharedPreferences mUserSharedPreference;
    private SharedPreferences.Editor mEditor;
    private final String PREFERENCE_KEY = "sorting";
    private final String SPINNER_SELECTION_KEY = "spinner";
    private SelectedMovieListener mSelectedMovieListener;
    private String mSortBy;


    public MainFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // allowing the fragment to have an option menue
        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView;
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //initializing all the view
        initViews(rootView);

        // initializing all variables
        initVariables();

        // set listeners
        setListeners();

        // assigning the mMovieAdapter to be the adapter of the mMainGrid
        mMainGrid.setAdapter(mMovieAdapter);
        mMainGrid.setEmptyView(mLoadingTextView);

        mSortBy = mUserSharedPreference.getString(PREFERENCE_KEY, Movie.POPULAR);
        fetchData();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_main_menu, menu);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.popularity:
                mSortBy = Movie.POPULAR;
                fetchData();
                changePreferenceTo(Movie.POPULAR);
                break;
            case R.id.most_rated:
                mSortBy = Movie.TOP_RATED;
                fetchData();
                changePreferenceTo(Movie.TOP_RATED);
                break;
            case R.id.favourites:
                mSortBy = Movie.FAVOURITE;
                fetchData();
                changePreferenceTo(Movie.FAVOURITE);
                break;
        }
        return true;
    }

    /**
     * Initializing all the views here
     *
     * @param rootView = this is the root view that holds the views which we will initialize
     */
    private void initViews(View rootView) {
        mMainGrid = (GridView) rootView.findViewById(R.id.main_grid_view);
        mLoadingTextView = (TextView) rootView.findViewById(R.id.main_grid_view_message);
        mLoadingProgressBar = (ProgressBar) rootView.findViewById(R.id.main_grid_view_progress);
        mSortingSpinner = new Spinner(getContext());
    }

    /**
     * this method is used to initialize all the variables used
     */
    private void initVariables() {
        mUserSharedPreference = PreferenceManager.getDefaultSharedPreferences(getContext());
        mMovieList = new ArrayList<>();
        mMovieAdapter = new MovieAdapter(getContext(), mMovieList);
    }

    /**
     * this method sets the listeners for clicks and other listeners
     */
    private void setListeners() {
        mMainGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // getting the selected movie
                Movie selectedMovie = mMovieList.get(position);
                // Referring to the main activity to decide to open intent to DetailsActivity or inflate a DetailsFragment
                // depending on the state of the device (tablet or not)
                mSelectedMovieListener.setSelectedMovie(selectedMovie, mSortBy);
            }
        });
    }


    /**
     * this method fetches the data from the api after being sure that we have a network connection
     * also gets the user preference of how to sort the movie list from the sharedPreferences
     */
    private void fetchData() {
        // starting to fetch data using the AsyncTask
        new FetchMovieList(getContext(), mSortBy, this).execute();
    }

    /**
     * this method writes to the shared preference a sorting option for the list (rating or popularity or favourite)
     *
     * @param newPreference = it should contain how will we request the data from the api (by rating or popularity)
     */
    private void changePreferenceTo(String newPreference) {
        mEditor = mUserSharedPreference.edit();
        mEditor.putString(PREFERENCE_KEY, newPreference);
        mEditor.commit();
    }

    // call back methods from the AsyncTaskCallBack interface
    @Override
    public void onPreExecuteCallBack() {
        mLoadingProgressBar.setVisibility(View.VISIBLE);
        mMovieAdapter.updateListData(new ArrayList<Movie>());
    }

    @Override
    public void onPostExecuteCallBackArrayList(ArrayList<Movie> movieList) {
        // preparing the fragment to be ready for viewing the list
        // making the progressBar disappear
        mLoadingProgressBar.setVisibility(View.GONE);
        //getting the movie list data
        mMovieList = movieList;
        // updating the data of the adapter
        mMovieAdapter.updateListData(mMovieList);
    }

    @Override
    public void onPostExecuteCallBackSingleMovie(Movie movie) {

    }

    public void setSelectedMovieListener(SelectedMovieListener selectedMovieListener) {
        mSelectedMovieListener = selectedMovieListener;
    }
}
