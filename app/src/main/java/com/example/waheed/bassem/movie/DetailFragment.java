package com.example.waheed.bassem.movie;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.database.DatabaseUtilsCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.waheed.bassem.movie.database.MovieDatabaseHandler;
import com.example.waheed.bassem.movie.database.PopularMovieDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * DetailFragment
 * this wil hold the detailed data about the movie
 * Original Title
 * Movie Poster
 * Plot Synopsis
 * Users Rating
 * Release Date
 */

public class DetailFragment extends Fragment implements View.OnClickListener, AsyncTaskCallBack {

    private TextView mMovieName;
    private TextView mAverageVote;
    private TextView mReleaseDate;
    private TextView mOverView;
    private ImageView mMoviePoster;
    private Movie mSelectedMovie;
    private LinearLayout mReviewLinearLayout;
    private LinearLayout mTrailerLinearLayout;
    private LinearLayout mTrialerLoading;
    private LinearLayout mReviewLoading;
    private Button mFavsButton;


    public DetailFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // inflating the layout
        View rootView;
        rootView = inflater.inflate(R.layout.fragment_detail, container, false);


        // initializing the views
        initViews(rootView);

        // set listeners
        setListeners();

        // getting the selectedMovie
        Bundle bundle = getArguments();
        mSelectedMovie = (Movie) bundle.getSerializable("selected_movie");
        String sortBy = bundle.getString("sort_by");

        new FetchMovieExtras(getContext(), sortBy, mSelectedMovie, this).execute();

        // filling the views with the data of the selectedMovie
        mMovieName.setText(mSelectedMovie.getMovieName());
        mAverageVote.setText(mSelectedMovie.getAverageVote());
        mReleaseDate.setText(mSelectedMovie.getReleaseDate());
        mOverView.setText(mSelectedMovie.getOverView());

        Picasso.with(getContext()).load(mSelectedMovie.getMovieImageUrl())
                .into(mMoviePoster);

        return rootView;
    }

    private void setListeners() {
        mFavsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MovieDatabaseHandler movieDatabaseHandler =
                        new MovieDatabaseHandler(MovieDatabaseHandler.FAVOURITE, getContext());
                movieDatabaseHandler.addMovie(mSelectedMovie);
                Toast.makeText(getContext(), "Movie added successfully to favourites", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Initializing all the views here
     *
     * @param rootView = this is the root view that holds the views which we will initialize
     */
    private void initViews(View rootView) {
        mMovieName = (TextView) rootView.findViewById(R.id.title_data_text_view);
        mAverageVote = (TextView) rootView.findViewById(R.id.vote_average_data_text_view);
        mReleaseDate = (TextView) rootView.findViewById(R.id.release_date_data_text_view);
        mOverView = (TextView) rootView.findViewById(R.id.over_view_data_text_view);
        mMoviePoster = (ImageView) rootView.findViewById(R.id.detail_image_view);
        mTrailerLinearLayout = (LinearLayout) rootView.findViewById(R.id.trailer_linear_layout);
        mReviewLinearLayout = (LinearLayout) rootView.findViewById(R.id.reviews_linear_layout);
        mTrialerLoading = (LinearLayout) rootView.findViewById(R.id.trailer_linear_layout_empty);
        mReviewLoading = (LinearLayout) rootView.findViewById(R.id.reviews_linear_layout_empty);
        mFavsButton = (Button) rootView.findViewById(R.id.favs_button);
    }


    /**
     * build the trailer child views and set listeners on them
     */
    private void buildTrailerChildViews() {
        if (!isNetworkConnected()) {
            // display no trailers available if we don't have trailers
            View view = LayoutInflater.from(getContext()).inflate(R.layout.trailer_list_item, mTrailerLinearLayout, false);
            Button titleButton = (Button) view.findViewById(R.id.trailer_list_item_button);
            titleButton.setText(getActivity().getString(R.string.no_connection_message));
            titleButton.setEnabled(false);
            mTrailerLinearLayout.addView(view);
        }
        // check if we have trailers
        else if (mSelectedMovie.getTrailers().size() > 0 && mSelectedMovie.getTrailers() != null) {
            ArrayList<Pair> trailerData = mSelectedMovie.getTrailers();
            for (int i = 0; i < trailerData.size(); i++) {
                Pair data = trailerData.get(i);
                String title = data.getTitle();
                View view = LayoutInflater.from(getContext()).inflate(R.layout.trailer_list_item, mTrailerLinearLayout, false);
                Button titleButton = (Button) view.findViewById(R.id.trailer_list_item_button);
                titleButton.setText(title);
                titleButton.setTag(i);
                titleButton.setOnClickListener(this);
                mTrailerLinearLayout.addView(view);
            }
        } else {
            // display no trailers available if we don't have trailers
            View view = LayoutInflater.from(getContext()).inflate(R.layout.trailer_list_item, mTrailerLinearLayout, false);
            Button titleButton = (Button) view.findViewById(R.id.trailer_list_item_button);
            titleButton.setText(getActivity().getString(R.string.trailers_not_available));
            titleButton.setEnabled(false);
            mTrailerLinearLayout.addView(view);
        }
    }

    /**
     * build the review child views
     */
    private void buildReviewChildViews() {
        if (!isNetworkConnected()) {
            // display no reviews available if we don't have reviews
            View view = LayoutInflater.from(getContext()).inflate(R.layout.review_list_item, mReviewLinearLayout, false);
            TextView authorTextView = (TextView) view.findViewById(R.id.author_list_item_text_view);
            TextView reviewTextView = (TextView) view.findViewById(R.id.review_list_item_text_view);
            authorTextView.setVisibility(View.GONE);
            reviewTextView.setText(getActivity().getString(R.string.no_connection_message));
            mReviewLinearLayout.addView(view);
        }
        // check if we have reviews
        else if (mSelectedMovie.getReviews().size() > 0 && mSelectedMovie.getReviews() != null) {
            ArrayList<Pair> reviewsData = mSelectedMovie.getReviews();
            for (Pair data : reviewsData) {
                String author = data.getTitle();
                String review = data.getBody();
                View view = LayoutInflater.from(getContext()).inflate(R.layout.review_list_item, mReviewLinearLayout, false);
                TextView authorTextView = (TextView) view.findViewById(R.id.author_list_item_text_view);
                TextView reviewTextView = (TextView) view.findViewById(R.id.review_list_item_text_view);
                authorTextView.setText(author);
                reviewTextView.setText(review);
                mReviewLinearLayout.addView(view);
            }
        } else {
            // display no reviews available if we don't have reviews
            View view = LayoutInflater.from(getContext()).inflate(R.layout.review_list_item, mReviewLinearLayout, false);
            TextView authorTextView = (TextView) view.findViewById(R.id.author_list_item_text_view);
            TextView reviewTextView = (TextView) view.findViewById(R.id.review_list_item_text_view);
            authorTextView.setVisibility(View.GONE);
            reviewTextView.setText(getActivity().getString(R.string.review_not_available));
            mReviewLinearLayout.addView(view);
        }
    }

    /**
     * erase the loading views by setting their visibility to be GONE
     */
    private void eraseLoadingViews() {
        mTrialerLoading.setVisibility(View.GONE);
        mReviewLoading.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        int id = (int) v.getTag();
        String url = mSelectedMovie.getTrailers().get(id).getBody();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        getActivity().startActivity(intent);
    }

    @Override
    public void onPreExecuteCallBack() {

    }

    @Override
    public void onPostExecuteCallBackArrayList(ArrayList<Movie> movieList) {

    }

    @Override
    public void onPostExecuteCallBackSingleMovie(Movie movie) {
        // updating the current selected movie
        mSelectedMovie = movie;
        // building the reviews and trailer views
        buildTrailerChildViews();
        buildReviewChildViews();
        eraseLoadingViews();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
