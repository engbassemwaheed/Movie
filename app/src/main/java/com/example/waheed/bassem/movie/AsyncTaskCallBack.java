package com.example.waheed.bassem.movie;

import java.util.ArrayList;

/**
 * this interface will be the connection between the MainActivity and the the FetchMovieList
 */

public interface AsyncTaskCallBack {
    void onPreExecuteCallBack();
    void onPostExecuteCallBackArrayList(ArrayList<Movie> movieList);
    void onPostExecuteCallBackSingleMovie(Movie movie);
}
