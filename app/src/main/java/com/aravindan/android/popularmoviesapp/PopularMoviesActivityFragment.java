package com.aravindan.android.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class PopularMoviesActivityFragment extends Fragment{

    private final String LOG_TAG = this.getClass().getName();
    private MoviePosterAdapter mMoviePosterAdapter;
    private ArrayList<Movie> mMovies = new ArrayList<>();
    private int mPageNumber;
    private boolean mEndOfPagesToastDisplayed;
    public PopularMoviesActivityFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {
        //Log.d(LOG_TAG, "Inside onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_popular_movies, container, false);
        GridView moviesGridView = (GridView)rootView.findViewById(R.id.gridview_movies_list);
        mMoviePosterAdapter = new MoviePosterAdapter(getActivity());
        moviesGridView.setAdapter(mMoviePosterAdapter);
        moviesGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem != 0 && firstVisibleItem + visibleItemCount >= totalItemCount){
                    if (mPageNumber < 1000) {
                        executeMoviesTask("append");
                    }
                    else{
                        if (view.getLastVisiblePosition()+1==totalItemCount && !mEndOfPagesToastDisplayed && mPageNumber == 1000){
                            Toast endOfPagesToast = Toast.makeText(getActivity().getApplicationContext(), "You're at the end of the list.", Toast.LENGTH_SHORT);
                            endOfPagesToast.show();
                            mEndOfPagesToastDisplayed = true;
                        }
                    }
                }

            }
        });

        moviesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MoviePosterAdapter adapter = (MoviePosterAdapter) parent.getAdapter();
                Movie movie = adapter.getItem(position);
                Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                intent.putExtra("MOVIE_DETAIL", movie);
                startActivity(intent);
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Log.d(LOG_TAG, "Inside onActivityCreated.. ");

        if (savedInstanceState == null){
            //Log.d(LOG_TAG, "Inside onActivityCreated..saved instance is null, hence, executing task to get from web ");
            mEndOfPagesToastDisplayed = false;
            executeMoviesTask("refresh");
        }else{
            //Log.d(LOG_TAG, "Inside onActivityCreated..saved instance is not empty, hence, taking data from parcelable ");
            mEndOfPagesToastDisplayed = false;
            mMovies = savedInstanceState.getParcelableArrayList("MOVIES_LIST");
            mPageNumber = savedInstanceState.getInt("PAGE_NUMBER");
            //Log.d(LOG_TAG, "Inside onActivityCreated.. parcelable arraylist size:  "+mMovies.size());
            mMoviePosterAdapter.clear();
            for (int i=0; i<mMovies.size(); i++){
                mMoviePosterAdapter.addMovie(mMovies.get(i));
            }
            mMoviePosterAdapter.notifyDataSetChanged();
        }
        //Log.d(LOG_TAG, "Inside onActivityCreated.. called moviesTask.execute");

    }



    private void executeMoviesTask(String mode){
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String sortBy = preferences.getString(getString(R.string.pref_sort_by), getString(R.string.pref_sort_by_popularity));
        String apiKey = preferences.getString(getString(R.string.pref_api_key), getString(R.string.pref_api_key_value));
        MoviesTask moviesTask = new MoviesTask(getActivity(), mMoviePosterAdapter);
        //Log.d(LOG_TAG, "Inside executeMoviesTask.. pageNumber : " + mPageNumber);
        if (isInternetAvailable()){
            if ("refresh".equals(mode)){
                mPageNumber = 1;
                mEndOfPagesToastDisplayed = false;
                moviesTask.execute(sortBy, apiKey, mode, Integer.toString(mPageNumber));
            }else{
                mPageNumber++;
                moviesTask.execute(sortBy, apiKey, mode, Integer.toString(mPageNumber));
            }
        }

        //Log.d(LOG_TAG, "Inside executeMoviesTask.. called moviesTask.execute");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Log.d(LOG_TAG, "Inside Fragment onOptionsItemSelected..");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort_highest_rated) {
            SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor prefEditor = preferences.edit();
            prefEditor.putString(getString(R.string.pref_sort_by), getString(R.string.pref_sort_by_rating));
            prefEditor.commit();
            executeMoviesTask("refresh");
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort_most_popular) {
            SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor prefEditor = preferences.edit();
            prefEditor.putString(getString(R.string.pref_sort_by), getString(R.string.pref_sort_by_popularity));
            prefEditor.commit();
            executeMoviesTask("refresh");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        //SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        //SharedPreferences.Editor prefEditor = preferences.edit();
        //prefEditor.putInt(getString(R.string.pref_page_number), R.integer.page_number);
        //prefEditor.commit();
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Log.d(LOG_TAG, "onSaveInstanceState is called.");
        //Log.d(LOG_TAG, "onSaveInstanceState mMoviePosterAdapter count: " + mMoviePosterAdapter.getCount());
        //Log.d(LOG_TAG, "onSaveInstanceState mMovies count: "+mMovies.size());
        /*for(int i = 0; i < mMovies.size(); i++){
            Log.d(LOG_TAG, "Movie in local variable : "+mMovies.get(i).getMovieTitle());
        }
        */
        mMovies.clear();

        for (int i = 0; i < mMoviePosterAdapter.getCount(); i++){
            //Log.d(LOG_TAG, "Movie in adapter child : "+mMoviePosterAdapter.getItem(i).getMovieTitle());
            mMovies.add(mMoviePosterAdapter.getItem(i));
        }

        //Log.d(LOG_TAG, "onSaveInstanceState Page Number : "+mPageNumber);
        outState.putParcelableArrayList("MOVIES_LIST", mMovies);
        outState.putInt("PAGE_NUMBER", mPageNumber);
        super.onSaveInstanceState(outState);
    }

    public boolean isInternetAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(getActivity().getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean returnValue = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if (!returnValue){
            Toast noInternetToast = Toast.makeText(getActivity().getApplicationContext(), "Internet Unavailable", Toast.LENGTH_SHORT);
            noInternetToast.show();
        }

        return returnValue;
    }
}
