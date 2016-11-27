package com.example.waheed.bassem.movie.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.example.waheed.bassem.movie.Pair;

import java.util.ArrayList;

/**
 * the database class that will hold the POPULAR movie data for data persistence
 */
public class PopularMovieDatabase extends Model {
    @Column (name = "MovieId")
    private String mMovieId;
    @Column (name = "Name")
    private String mName;
    @Column (name = "AverageVote")
    private String mAverageVote;
    @Column (name = "ReleaseDate")
    private String mReleaseDate;
    @Column (name = "OverView")
    private String mOverView;
    @Column (name = "ImageUrl")
    private String mImageUrl;
    @Column (name = "Reviews")
    private ArrayList<Pair> mReviewsArrayList;
    @Column (name = "Trailers")
    private ArrayList<Pair> mTrailerArrayList;

    /**
     * default constructor
     */
    public PopularMovieDatabase() {
    }

    /**
     * this is the constructor we will use to create a new intity in the database
     * @param movieId = the movie id !
     * @param name = the movie title
     * @param averageVote = movie average votes
     * @param releaseDate = movie release date
     * @param overView = movie over view
     * @param imageUrl = the image url (will be used by picasso to retrieve the image in the offline mode)
     */
    public PopularMovieDatabase(String movieId, String name, String averageVote, String releaseDate,
                                String overView, String imageUrl) {
        mMovieId = movieId;
        mName = name;
        mAverageVote = averageVote;
        mReleaseDate = releaseDate;
        mOverView = overView;
        mImageUrl = imageUrl;
        mReviewsArrayList = new ArrayList<>();
        mTrailerArrayList = new ArrayList<>();
    }

    /**
     * setting the review data
     * note that the reviews data are not set in the constructor
     * @param reviews = the review data in the form of array list of Pair
     */
    public void setReviews (ArrayList<Pair> reviews) {
        mReviewsArrayList = reviews;
    }

    /**
     * setting the trailers data
     * note that the trailers data are not set in the constructor
     * @param trailers = the trailers data in the form of array list of Pair
     */
    public void setTrailers (ArrayList<Pair> trailers) {
        mTrailerArrayList = trailers;
    }


    /**
     * getter method for mMovieId
     * @return = the movie id
     */
    public String getMovieId () {
        return mMovieId;
    }

    /**
     * getter method for mName
     * @return = the movie title
     */
    public String getName () {
        return mName;
    }

    /**
     * getter method for mAverageVote
     * @return = the movie average vote
     */
    public String getAverageVote () {
        return mAverageVote;
    }

    /**
     * getter method for mReleaseDate
     * @return = the movie release date
     */
    public String getReleaseDate () {
        return mReleaseDate;
    }

    /**
     * getter method for mOverView
     * @return = the movie over view
     */
    public String getOverView () {
        return mOverView;
    }

    /**
     * getter method for mImageUrl
     * @return = the movie Poster Url
     */
    public String getImageUrl () {
        return mImageUrl;
    }

    /**
     * getter method for mReviewsArrayList
     * @return = the movie reviews in the form of array list of Pair
     */
    public ArrayList<Pair> getReviews () {
        return mReviewsArrayList;
    }

    /**
     * getter method for mTrailersArrayList
     * @return = the movie trailers in the form of array list of Pair
     */
    public ArrayList<Pair> getTrailers () {
        return mTrailerArrayList;
    }



}
