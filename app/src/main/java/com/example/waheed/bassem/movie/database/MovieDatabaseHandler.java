package com.example.waheed.bassem.movie.database;

import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.example.waheed.bassem.movie.Movie;
import com.example.waheed.bassem.movie.Pair;

import java.util.ArrayList;
import java.util.List;


/**
 * this class will be used to handle the PopularMovieDatabase class CRUD operations
 */
public class MovieDatabaseHandler {

    public static final int POPULAR = 1;
    public static final int MOST_RATED = 2;
    public static final int FAVOURITE = 3;
    private int mDatabaseTable;

    public MovieDatabaseHandler(int databaseTable, Context context) {
        ActiveAndroid.initialize(context);
        mDatabaseTable = databaseTable;
    }

    /**
     * adding the movie array list to the database
     *
     * @param movieArrayList = the movie array list fetched from the API
     */
    public void addMovieArrayList(ArrayList<Movie> movieArrayList) {
        for (Movie movie : movieArrayList) {
            addMovie(movie);
        }
    }

    /**
     * gets an array list of all movies in the data base
     *
     * @return = the array list of movies
     */
    public ArrayList<Movie> getMovieArrayList() {
        List<Model> movieDatabaseArrayList = new ArrayList<>();
        switch (mDatabaseTable) {
            case POPULAR:
                movieDatabaseArrayList = new Select().from(PopularMovieDatabase.class).execute();
                break;
            case MOST_RATED:
                movieDatabaseArrayList = new Select().from(MostRatedMovieDatabase.class).execute();
                break;
            case FAVOURITE:
                movieDatabaseArrayList = new Select().from(FavouriteMovieDatabase.class).execute();
                break;
            default:
                break;
        }
        ArrayList<Movie> movieArrayList = new ArrayList<>();
        for (int i = 0; i < movieDatabaseArrayList.size(); i++) {
            movieArrayList.add(getMovie(movieDatabaseArrayList.get(i)));
        }
        return movieArrayList;
    }

    /**
     * adds the trailer and reviews data to the PopularMovieDatabase
     *
     * @param movieId  = the id of the movie we want to update
     * @param trailers = the trailers data in array list of Pair form
     * @param reviews  = = the reviews data in array list of Pair form
     */
    public void addTrailersAndReviews(String movieId, ArrayList<Pair> trailers, ArrayList<Pair> reviews) {
        switch (mDatabaseTable) {
            case POPULAR:
                PopularMovieDatabase popularMovieDatabase =
                        new Select().from(PopularMovieDatabase.class).where("MovieId = ?", movieId).executeSingle();
                popularMovieDatabase.setReviews(reviews);
                popularMovieDatabase.setTrailers(trailers);
                popularMovieDatabase.save();
                break;
            case MOST_RATED:
                MostRatedMovieDatabase mostRatedMovieDatabase =
                        new Select().from(MostRatedMovieDatabase.class).where("MovieId = ?", movieId).executeSingle();
                mostRatedMovieDatabase.setReviews(reviews);
                mostRatedMovieDatabase.setTrailers(trailers);
                mostRatedMovieDatabase.save();
                break;
            case FAVOURITE:
                FavouriteMovieDatabase favouriteMovieDatabase =
                        new Select().from(FavouriteMovieDatabase.class).where("MovieId = ?", movieId).executeSingle();
                favouriteMovieDatabase.setReviews(reviews);
                favouriteMovieDatabase.setTrailers(trailers);
                favouriteMovieDatabase.save();
                break;
            default:
                break;
        }
    }

    /**
     * erasing the database
     */
    public void deleteAllMovies() {
        switch (mDatabaseTable) {
            case POPULAR:
                new Delete().from(PopularMovieDatabase.class).execute();
                break;
            case MOST_RATED:
                new Delete().from(MostRatedMovieDatabase.class).execute();
                break;
            case FAVOURITE:
                new Delete().from(FavouriteMovieDatabase.class).execute();
                break;
            default:
                break;
        }
    }

    /**
     * returns a movie by specifying its id
     *
     * @param movieId = the id of the required movie
     * @return = the required movie
     */
    public Movie getMovieById(String movieId) {
        Movie movie = new Movie();
        switch (mDatabaseTable) {
            case POPULAR:
                PopularMovieDatabase popularMovieDatabase =
                        new Select().from(PopularMovieDatabase.class).where("MovieId = ?", movieId).executeSingle();
                movie = getMovie(popularMovieDatabase);
                break;
            case MOST_RATED:
                MostRatedMovieDatabase mostRatedMovieDatabase =
                        new Select().from(MostRatedMovieDatabase.class).where("MovieId = ?", movieId).executeSingle();
                movie = getMovie(mostRatedMovieDatabase);
                break;
            case FAVOURITE:
                FavouriteMovieDatabase favouriteMovieDatabase =
                        new Select().from(FavouriteMovieDatabase.class).where("MovieId = ?", movieId).executeSingle();
                movie = getMovie(favouriteMovieDatabase);
                break;
            default:
                break;
        }

        return movie;

    }

    /**
     * adding a movie to PopularMovieDatabase
     *
     * @param movie = the movie we want to save
     */
    public void addMovie(Movie movie) {
        switch (mDatabaseTable) {
            case POPULAR:
                PopularMovieDatabase popularMovieDatabase = new PopularMovieDatabase(movie.getMovieID(),
                        movie.getMovieName(),
                        movie.getAverageVote(),
                        movie.getReleaseDate(),
                        movie.getOverView(),
                        movie.getMovieImageUrl());
                popularMovieDatabase.save();
                break;
            case MOST_RATED:
                MostRatedMovieDatabase mostRatedMovieDatabase = new MostRatedMovieDatabase(movie.getMovieID(),
                        movie.getMovieName(),
                        movie.getAverageVote(),
                        movie.getReleaseDate(),
                        movie.getOverView(),
                        movie.getMovieImageUrl());
                mostRatedMovieDatabase.save();
                break;
            case FAVOURITE:
                FavouriteMovieDatabase favouriteMovieDatabase = new FavouriteMovieDatabase(movie.getMovieID(),
                        movie.getMovieName(),
                        movie.getAverageVote(),
                        movie.getReleaseDate(),
                        movie.getOverView(),
                        movie.getMovieImageUrl());
                favouriteMovieDatabase.save();
                break;
            default:
                break;
        }
    }

    /**
     * returns a movie from a PopularMovieDatabase object
     * (converts a PopularMovieDatabase to a Movie !)
     *
     * @param movieDatabase = an object from the PopularMovieDatabase
     * @return = the movie
     */
    private Movie getMovie(Model movieDatabase) {
        Movie movie = new Movie();
        switch (mDatabaseTable) {
            case POPULAR:
                PopularMovieDatabase popularMovieDatabase = (PopularMovieDatabase) movieDatabase;
                movie = new Movie(popularMovieDatabase.getName(),
                        popularMovieDatabase.getImageUrl(),
                        popularMovieDatabase.getOverView(),
                        popularMovieDatabase.getReleaseDate(),
                        popularMovieDatabase.getAverageVote(),
                        popularMovieDatabase.getMovieId());
                movie.setImageUrl(popularMovieDatabase.getImageUrl());
                movie.setTrailers(popularMovieDatabase.getTrailers());
                movie.setReviews(popularMovieDatabase.getReviews());
                break;
            case MOST_RATED:
                MostRatedMovieDatabase mostRatedMovieDatabase = (MostRatedMovieDatabase) movieDatabase;
                movie = new Movie(mostRatedMovieDatabase.getName(),
                        mostRatedMovieDatabase.getImageUrl(),
                        mostRatedMovieDatabase.getOverView(),
                        mostRatedMovieDatabase.getReleaseDate(),
                        mostRatedMovieDatabase.getAverageVote(),
                        mostRatedMovieDatabase.getMovieId());
                movie.setImageUrl(mostRatedMovieDatabase.getImageUrl());
                movie.setTrailers(mostRatedMovieDatabase.getTrailers());
                movie.setReviews(mostRatedMovieDatabase.getReviews());
                break;
            case FAVOURITE:
                FavouriteMovieDatabase favouriteMovieDatabase = (FavouriteMovieDatabase) movieDatabase;
                movie = new Movie(favouriteMovieDatabase.getName(),
                        favouriteMovieDatabase.getImageUrl(),
                        favouriteMovieDatabase.getOverView(),
                        favouriteMovieDatabase.getReleaseDate(),
                        favouriteMovieDatabase.getAverageVote(),
                        favouriteMovieDatabase.getMovieId());
                movie.setImageUrl(favouriteMovieDatabase.getImageUrl());
                movie.setTrailers(favouriteMovieDatabase.getTrailers());
                movie.setReviews(favouriteMovieDatabase.getReviews());
                break;
            default:
                break;
        }
        return movie;
    }


}
