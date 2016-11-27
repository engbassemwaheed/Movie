package com.example.waheed.bassem.movie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements SelectedMovieListener {

    private boolean mIsTablet;
    private MainFragment mMainFragment;
    private DetailFragment mDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // creating new instance of MainFragment
        mMainFragment = new MainFragment();
        // setting the SelectedMovieListener
        mMainFragment.setSelectedMovieListener(this);
        // inflating the fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_frame, mMainFragment)
                .commit();

        // checking for the tablet mode
        if (findViewById(R.id.details_frame) != null) {
            mIsTablet = true;
        }
    }

    @Override
    public void setSelectedMovie(Movie selectedMovie, String sortBy) {
        // putting the selectedMovie in a bundle
        Bundle bundle = new Bundle();
        bundle.putSerializable("selected_movie", selectedMovie);
        bundle.putString("sort_by", sortBy);

        // checking for tablet mode
        if (mIsTablet) {
            // we are in tablet mode !
            // creating a new fragment and setting the bundle to be an argument
            mDetailFragment = new DetailFragment();
            mDetailFragment.setArguments(bundle);

            // inflating new fragment in  R.id.details_frame
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.details_frame, mDetailFragment)
                    .commit();
        } else {
            // we are in mobile mode !!
            // making an intent instance to lunch the detail activity
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            // adding the bundle
            intent.putExtra("movie_bundle", bundle);

            //starting the detail activity
            startActivity(intent);
        }
    }
}
