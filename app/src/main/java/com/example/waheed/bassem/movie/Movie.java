package com.example.waheed.bassem.movie;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Movie class which will hold the whole movie data
 */

public class Movie implements Serializable{

    public static final String POPULAR = "popular";
    public static final String TOP_RATED = "top_rated";
    public static final String FAVOURITE = "favourite";
    private String mMovieName;
    private String mMovieImageUrl;
    private String mOverView;
    private String mReleaseDate;
    private String mAverageVote;
    private ArrayList<Pair> mReviewsArrayList;
    private ArrayList<Pair> mTrailerArrayList;
    private String mMovieId;
    private final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    private final String TRAILER_FIXED_URL = "/videos?";
    private final String REVIEW_FIXED_URL = "/reviews?";

    private final String API_KEY = "api_key=";
    private final String APP_ID = "203b6ca87d8f7410193e8f740928dae2";

    private final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p";
    private final String IMAGE_SCALING_TYPE = "/w342";


    public Movie (){

    }
    /**
     * Constructor, will be use to populate the Movie class with data
     * @param movieName = the movie name
     * @param movieImage = the movie image address to be fetched by Picasso
     * @param overView = over view about the movie
     * @param releaseDate = movie release date
     * @param averageVote = average voting for the movie
     */
    public Movie (String movieName, String movieImage, String overView, String releaseDate, String averageVote, String id){
        mMovieName = movieName;
        mMovieImageUrl = IMAGE_BASE_URL + IMAGE_SCALING_TYPE + movieImage;
        mOverView = overView;
        mReleaseDate = releaseDate;
        mAverageVote = averageVote;
        mMovieId = id;
    }

    /**
     * sets the image URL
     * @param imageUrl = the new image URL
     */
    public void setImageUrl (String imageUrl) {
        mMovieImageUrl = imageUrl;
    }

    /**
     * getter method for movie name
     * @return = the movie name
     */
    public String getMovieName () {
        return mMovieName;
    }

    /**
     * getter method for movie Image
     * @return = the movie image address to be fetched by Picasso
     */
    public String getMovieImageUrl() {
        return mMovieImageUrl;
    }

    /**
     * getter method for movie over view
     * @return = the over view of the movie
     */
    public String getOverView () {
        return mOverView;
    }

    /**
     * getter method for the release date
     * @return = the release date of the movie
     */
    public String getReleaseDate () {
        return mReleaseDate;
    }


    /**
     * getter method for the vote average of the movie
     * @return = the vote average
     */
    public String getAverageVote () {
        return mAverageVote;
    }

    /**
     * setter method for trailers
     * @param trailers = the movie trailers
     */
    public void setTrailers (ArrayList<Pair> trailers) {
        mTrailerArrayList = trailers;
    }

    /**
     * setter method for reviews
     * @param reviews = the movie reviews
     */
    public void setReviews (ArrayList<Pair> reviews) {
        mReviewsArrayList = reviews;
    }

    /**
     * getter method for the movie trailers
     * @return = movie trailers
     */
    public ArrayList<Pair> getTrailers () {
        return mTrailerArrayList;
    }

    /**
     * getter method for the movie reviews
     * @return = the movie reviews
     */
    public ArrayList<Pair> getReviews () {
        return mReviewsArrayList;
    }

    /**
     * returns the url to request the trailers and my return null if we have an exception in generating the URL
     * @return = URL for requesting the trailers
     */
    public URL getTrailersURL () {
        try {
            return new URL(BASE_URL + mMovieId + TRAILER_FIXED_URL + API_KEY + APP_ID);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * returns the url to request the reviews and my return null if we have an exception in generating the URL
     * @return = URL for requesting the reviews
     */
    public URL getReviewsURL () {
        try {
            return new URL(BASE_URL + mMovieId + REVIEW_FIXED_URL + API_KEY + APP_ID);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * getter method for mMovieId
     * @return = the id of the movie
     */
    public String getMovieID () {
        return mMovieId;
    }
}
