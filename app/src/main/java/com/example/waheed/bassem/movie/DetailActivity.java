package com.example.waheed.bassem.movie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DetailActivity extends AppCompatActivity {
    private DetailFragment mDetailFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            mDetailFragment = new DetailFragment();
            mDetailFragment.setArguments(getIntent().getBundleExtra("movie_bundle"));
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.activity_detail_container, mDetailFragment)
                    .commit();
        }
    }
}
