package com.aravindan.android.popularmoviesapp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

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
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Aravindan on 17/7/2015.
 */
public class MoviesTask extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = MoviesTask.class.getName();
    private Context mContext;
    private String mQueryResult;
    private String mMode;
    private ArrayList<Movie> mMovies = new ArrayList<>();
    private MoviePosterAdapter mMoviePosterAdapter;
    public MoviesTask(Context context, MoviePosterAdapter moviePosterAdapter){
        mContext = context;
        mMoviePosterAdapter = moviePosterAdapter;
        mMode = "refresh";
    }

    private void getMovieDetailsFromQueryResult(){
        final String MOVIES_LIST = "results";
        final String MOVIE_ID = "id";
        final String MOVIE_TITLE = "title";
        final String MOVIE_POSTER_PATH = "poster_path";
        final String MOVIE_RELEASE_DATE = "release_date";
        final String MOVIE_VOTE_AVERAGE = "vote_average";
        final String MOVIE_OVERVIEW = "overview";

        try{
            JSONObject moviesJson = new JSONObject(mQueryResult);
            JSONArray resultsArray = moviesJson.getJSONArray(MOVIES_LIST);

            for (int i=0; i < resultsArray.length(); i++){
                JSONObject movieDetails = resultsArray.getJSONObject(i);
                String movieID = movieDetails.getString(MOVIE_ID);
                String movieTitle = movieDetails.getString(MOVIE_TITLE);
                String moviePosterPath = movieDetails.getString(MOVIE_POSTER_PATH);
                String movieReleaseDate = movieDetails.getString(MOVIE_RELEASE_DATE);
                String movieVoteAverage = movieDetails.getString(MOVIE_VOTE_AVERAGE);
                String movieOverview = movieDetails.getString(MOVIE_OVERVIEW);

                mMovies.add(new Movie(movieID, movieTitle, moviePosterPath, movieReleaseDate, movieVoteAverage, movieOverview));
            }
        }catch(JSONException e1){
            Log.e(LOG_TAG, e1.getMessage(), e1);
            e1.printStackTrace();
        }

    }

    @Override
    protected Void doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try{
            final String MOVIES_URL = "https://api.themoviedb.org/3/discover/movie";
            final String SORT_PARAM = "sort_by";
            final String API_KEY_PARAM = "api_key";
            final String PAGE = "page";
            String sortParam = params[0];
            String apiKeyParam = params[1];
            mMode = params[2];
            String pageNumber = params[3];
            Uri builtUri = Uri.parse(MOVIES_URL);
            builtUri = builtUri.buildUpon().appendQueryParameter(SORT_PARAM, sortParam+".desc").appendQueryParameter(PAGE, pageNumber).appendQueryParameter(API_KEY_PARAM, apiKeyParam).build();
            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if (inputStream==null){
                //nothing to do
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                return null;
            }
            mQueryResult = buffer.toString();


        }catch(MalformedURLException e1){
            Log.e(LOG_TAG, e1.getMessage(), e1);
            e1.printStackTrace();
        }catch (IOException e2){
            Log.e(LOG_TAG, e2.getMessage(), e2);
            e2.printStackTrace();
        }finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        getMovieDetailsFromQueryResult();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        //Log.d(LOG_TAG, mQueryResult);

        if(!mMoviePosterAdapter.isEmpty() && "refresh".equals(mMode)){
            mMoviePosterAdapter.clear();
        }
        for (int i=0; i<mMovies.size(); i++){
            Movie movie = mMovies.get(i);
            //Log.d(LOG_TAG, "Movie Name : " + movie.getMovieTitle() + ", Poster Path : " + movie.getMoviePosterPath() + ", Movie ID : " + movie.getMovieID());
            mMoviePosterAdapter.addMovie(movie);
        }
        mMoviePosterAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
