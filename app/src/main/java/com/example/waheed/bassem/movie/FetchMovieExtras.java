package com.example.waheed.bassem.movie;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.example.waheed.bassem.movie.database.MovieDatabaseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by basse on 11/26/2016.
 */

public class FetchMovieExtras extends AsyncTask <Void, Void, Movie> {

    private final String REQUEST_METHOD = "GET";

    private AsyncTaskCallBack mAsyncTaskCallBack;

    private Movie mMovie;

    private boolean mIsNetworkConnected;

    private MovieDatabaseHandler mMovieDatabaseHandler;


    public FetchMovieExtras (Context context, String databaseTable, Movie movie, AsyncTaskCallBack asyncTaskCallBack) {
        // setting the AsyncTaskCallBack
        mAsyncTaskCallBack = asyncTaskCallBack;
        mMovie = movie;

        if(databaseTable.equals(Movie.POPULAR)) {
            mMovieDatabaseHandler = new MovieDatabaseHandler(MovieDatabaseHandler.POPULAR, context);
        } else if (databaseTable.equals(Movie.TOP_RATED)){
            mMovieDatabaseHandler = new MovieDatabaseHandler(MovieDatabaseHandler.MOST_RATED, context);
        } else if (databaseTable.equals(Movie.FAVOURITE)){
            mMovieDatabaseHandler = new MovieDatabaseHandler(MovieDatabaseHandler.FAVOURITE, context);
        }

        // checking fro network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        mIsNetworkConnected = (networkInfo != null && networkInfo.isConnected());
    }


    /**
     * this method's function is to get a JSON string from the API
     * @return = the required JSON String and it could be null in several cases
     * 1- inputStream is null (no inputStream received)
     * 2- buffer length is 0 (buffer is empty)
     * 3- IOException occurs
     * so we will need to handle those null cases
     */
    private String getJsonString (URL url) {
        // checking that the url provided is not null
        if (url == null) {
            return null;
        }

        // declaring and initializing the variable we will need
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        String line = null;
        StringBuilder buffer = new StringBuilder();
        String jsonString = null;

        // trying to get our jsonString
        try {
            // opening connection
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(REQUEST_METHOD);
            connection.connect();

            // getting the response of the API
            // and making sure it is not empty, if it's empty we return null
            inputStream = connection.getInputStream();
            if (inputStream == null) {
                return null;
            }
            // getting our readers ready (the bufferedReader)
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);

            // reading line by line from the bufferedReader and store them in a String line
            // then adding this String line to the StringBuilder buffer
            // and insuring that it isn't empty, if it's empty we return null
            line = bufferedReader.readLine();
            while (line != null) {
                buffer.append(line);
                line = bufferedReader.readLine();
            }
            if (buffer.length() == 0) {
                return null;
            }

            // getting out jsonString from the StringBuilder
            jsonString = buffer.toString();

        } catch (IOException e) {
            // handling the exception and returning null
            e.printStackTrace();
            return null;
        }
        finally {
            // disconnecting the connection
            // and make sure that it was opened before (isn't null)
            if (connection != null) {
                connection.disconnect();
            }
            // closing the inputStream
            // and make sure that it was opened before (isn't null)
            // and handle the exception if it wasn't closed
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // handling the exception and returning null
                    e.printStackTrace();
                }
            }
        }
        // returning our beloved jsonString :D
        return jsonString;
    }


    /**
     * parses a string to get the trailers items (name and key)
     * @param jsonString = the input json string
     * @return = ArrayList of Pair (trailers)
     */
    private ArrayList<Pair> parseJsonForTrailers (String jsonString) {
        // making instance of ArrayList<Pair>
        ArrayList<Pair> trailers = new ArrayList<>();

        // checking for the null of emptiness of the jsonArray
        // and if null of empty return null
        if (jsonString == null || jsonString.isEmpty()) {
            return null;
        }

        // trying parsing the jsonArray
        try {
            // getting the root object of the jsonString
            JSONObject rootObject = new JSONObject(jsonString);

            // getting the array named "result" which contains the movies
            JSONArray resultArray = rootObject.getJSONArray("results");

            // getting the movies data and storing them in the movieList
            for (int i = 0; i<resultArray.length(); i++) {
                JSONObject resultItem = resultArray.getJSONObject(i);
                // checking if the video is on YouTube
                if (resultItem.optString("site").equals("YouTube")){
                    String name = resultItem.optString("name");
                    String key = resultItem.optString("key");
                    trailers.add(new Pair(name, convertToYouTubeURL(key)));
                }
            }
        } catch (JSONException e) {
            // handling the exception and returning null
            e.printStackTrace();
            return null;
        }
        return trailers;
    }

    /**
     * parses a string to get the reviews items (author and content)
     * @param jsonString = the input json string
     * @return = ArrayList of Pair (reviews)
     */
    private ArrayList<Pair> parseJsonForReviews (String jsonString) {
        // making instance of ArrayList<Pair>
        ArrayList<Pair> reviews = new ArrayList<>();

        // checking for the null of emptiness of the jsonArray
        // and if null of empty return null
        if (jsonString == null || jsonString.isEmpty()) {
            return null;
        }

        // trying parsing the jsonArray
        try {
            // getting the root object of the jsonString
            JSONObject rootObject = new JSONObject(jsonString);

            // getting the array named "result" which contains the movies
            JSONArray resultArray = rootObject.getJSONArray("results");

            // getting the movies data and storing them in the movieList
            for (int i = 0; i<resultArray.length(); i++) {
                JSONObject resultItem = resultArray.getJSONObject(i);
                String author = resultItem.optString("author");
                String content = resultItem.optString("content");
                reviews.add(new Pair(author, content));
            }
        } catch (JSONException e) {
            // handling the exception and returning null
            e.printStackTrace();
            return null;
        }
        return reviews;
    }


    /**
     * adds the key to a fixed youtube link to make a complete URL
     * @param key = the key of the trailer video
     * @return = the URL of the video
     */
    private String convertToYouTubeURL (String key) {
        return "https://www.youtube.com/watch?v=" + key;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mAsyncTaskCallBack.onPreExecuteCallBack();
    }

    @Override
    protected Movie doInBackground(Void... params) {
        Movie result;
        if (mIsNetworkConnected) {
            String trailerJsonString = getJsonString(mMovie.getTrailersURL());
            ArrayList<Pair> trailers = parseJsonForTrailers(trailerJsonString);
            mMovie.setTrailers(trailers);
            String reviewsJsonString = getJsonString(mMovie.getReviewsURL());
            ArrayList<Pair> reviews = parseJsonForReviews(reviewsJsonString);
            mMovie.setReviews(reviews);
            mMovieDatabaseHandler.addTrailersAndReviews(mMovie.getMovieID(), trailers, reviews);
            result = mMovie;
        } else {
            result = mMovieDatabaseHandler.getMovieById(mMovie.getMovieID());
        }
        return result;
    }

    @Override
    protected void onPostExecute(Movie movie) {
        mAsyncTaskCallBack.onPostExecuteCallBackSingleMovie(movie);
    }
}
