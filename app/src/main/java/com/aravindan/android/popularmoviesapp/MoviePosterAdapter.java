package com.aravindan.android.popularmoviesapp;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Aravindan on 18/7/2015.
 */
public class MoviePosterAdapter extends BaseAdapter {
    private final String LOG_TAG = this.getClass().getName();
    private Context mContext;
    private final String mImageBasePath;
    private ArrayList<Movie> mMovies = new ArrayList<>();

    public MoviePosterAdapter(Context context, ArrayList<Movie> movies){
        //Log.d(LOG_TAG, "Initializing adapter...");
        mContext = context;
        mImageBasePath = context.getString(R.string.movie_poster_base_path);
        mMovies = movies;
    }

    @Override
    public int getCount() {
        Log.d(LOG_TAG, "Getting count of movies..."+mMovies.size());
        return mMovies.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Log.d(LOG_TAG, "Inside Get View : ");
        ImageView imageView;
        if (convertView == null){
            imageView = new ImageView(mContext);
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0,0,0,0);

        } else {
            imageView = (ImageView) convertView;
        }
        Movie movie = mMovies.get(position);
        String moviePosterPath = movie.getMoviePosterPath();
        Uri imageUri = Uri.parse(mImageBasePath).buildUpon().appendEncodedPath(moviePosterPath).build();
        //Log.d(LOG_TAG, imageUri.toString());
        Picasso.with(mContext).load(imageUri).into(imageView);
        return imageView;

    }

    @Override
    public Object getItem(int position) {
        return mMovies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void addMovie(Movie movie){
        mMovies.add(movie);
    }

    @Override
    public boolean isEmpty() {

        if (mMovies.size()==0)
            return true;
        else
            return false;
    }

    public void clear(){
        mMovies.clear();
    }
}
