package com.example.waheed.bassem.movie;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;

import com.activeandroid.ActiveAndroid;
import com.example.waheed.bassem.movie.database.MostRatedMovieDatabase;
import com.example.waheed.bassem.movie.database.MovieDatabaseHandler;
import com.example.waheed.bassem.movie.database.PopularMovieDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * this class is used to fetch the movies from the API
 */

public class FetchMovieList extends AsyncTask <Void, Void, ArrayList<Movie>>{
    private final String BASE_URL = "http://api.themoviedb.org/3/movie/";

    private final String QUESTION_MARK = "?";

    private final String INCLUDE_ADULT = "include_adult";
    private final String INCLUDE_ADULT_STATE = "false";

    private final String API_KEY = "api_key";
    private final String APP_ID = "[API_KEY]";

    private final String REQUEST_METHOD = "GET";

    private AsyncTaskCallBack mAsyncTaskCallBack;

    private String mSortBy;

    private ArrayList<Movie> mMovieArrayList;

    private MovieDatabaseHandler mMovieDatabaseHandler;

    private boolean mIsNetworkConnected;

    /**
     * constructor is used to pass the way of sorting the data in the app
     * also if the user of the class used invalid String
     * the mSortby String will be set to POPULAR String
     * @param context = the context of the calling class
     * @param sortBy = the way of sorting the result coming from the API
     *               and it needs to be POPULAR or TOP_RATE Strings or it will be set
     *               to be POPULAR
     * @param asyncTaskCallBack = interface to set the data pre and post execution
     */
    public FetchMovieList (Context context, String sortBy, AsyncTaskCallBack asyncTaskCallBack) {

        mSortBy = sortBy;

        switch (mSortBy) {
            case Movie.POPULAR:
                mMovieDatabaseHandler = new MovieDatabaseHandler(MovieDatabaseHandler.POPULAR, context);
                break;
            case Movie.TOP_RATED:
                mMovieDatabaseHandler = new MovieDatabaseHandler(MovieDatabaseHandler.MOST_RATED, context);
                break;
            case Movie.FAVOURITE:
                mMovieDatabaseHandler = new MovieDatabaseHandler(MovieDatabaseHandler.FAVOURITE, context);
                break;
            default:
                mSortBy = Movie.POPULAR;
                mMovieDatabaseHandler = new MovieDatabaseHandler(MovieDatabaseHandler.POPULAR, context);
                break;
        }

        // setting the AsyncTaskCallBack
        mAsyncTaskCallBack = asyncTaskCallBack;

        // initializing the mMovieArrayList
        mMovieArrayList = new ArrayList<>();


        // checking fro network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        mIsNetworkConnected = (networkInfo != null && networkInfo.isConnected());
    }


    /**
     * this method builds the url that we will use to get the data from the API
     * @return = the url we built but need to check for MalformedURLException
     * @throws MalformedURLException
     */
    private URL buildUrl () {
        // Building up the urlString to put the user preferred sorting (mSortBy)
        // urlString example: http://api.themoviedb.org/3/movie/popular?
        String urlString = BASE_URL + mSortBy + QUESTION_MARK;

        // building up the rest of the URL and adding INCLUDE_ADULT filter and our APP_ID
        Uri baseUrl = Uri.parse(urlString);
        Uri.Builder urlBuilder = baseUrl.buildUpon();
        urlBuilder.appendQueryParameter(INCLUDE_ADULT, INCLUDE_ADULT_STATE);
        urlBuilder.appendQueryParameter(API_KEY, APP_ID);

        // getting our URL as String
        // note that this method throws and MalformedURLException
        String finalUrl = urlBuilder.toString();

        try {
            return new URL(finalUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
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
     * this method parse the jsonString to ArrayList of Movie
     * @param jsonString = the jsonString un-parsed
     * @return = an ArrayList of Movie populated with data
     */
    private ArrayList<Movie> parseJsonForMovieList(String jsonString) {
        // making instance of ArrayList<Movie>
        ArrayList<Movie> movieList = new ArrayList<Movie>();

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
                String posterPath = resultItem.optString("poster_path", "poster_path");
                String overView = resultItem.optString("overview", "overview");
                String releaseDate = resultItem.optString("release_date", "release_date");
                String title = resultItem.optString("title", "title");
                String averageVote = resultItem.optString("vote_average", "vote_average");
                String id = resultItem.getString("id");
                movieList.add(new Movie(title, posterPath, overView, releaseDate, averageVote, id));
            }
        } catch (JSONException e) {
            // handling the exception and returning null
            e.printStackTrace();
            return null;
        }

        // returning our sweet sweet movieList :D :D
        return movieList;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mAsyncTaskCallBack.onPreExecuteCallBack();
    }

    @Override
    protected ArrayList<Movie> doInBackground(Void... params) {
        ArrayList<Movie> result;
        if (mIsNetworkConnected && !mSortBy.equals(Movie.FAVOURITE)) {
            String jsonString = getJsonString(buildUrl());
            mMovieArrayList = parseJsonForMovieList(jsonString);
            mMovieDatabaseHandler.deleteAllMovies();
            mMovieDatabaseHandler.addMovieArrayList(mMovieArrayList);
            result = mMovieArrayList;
        } else if (mSortBy.equals(Movie.FAVOURITE)) {
            result = mMovieDatabaseHandler.getMovieArrayList();
        }
        else{
            result = mMovieDatabaseHandler.getMovieArrayList();
        }
        return result;
    }

    @Override
    protected void onPostExecute(ArrayList<Movie> movies) {
        mAsyncTaskCallBack.onPostExecuteCallBackArrayList(movies);
    }
}
