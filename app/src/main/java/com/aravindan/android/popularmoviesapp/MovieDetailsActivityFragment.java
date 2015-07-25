package com.aravindan.android.popularmoviesapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsActivityFragment extends Fragment {
    private final String LOG_TAG = getClass().getName();
    private String mImageBasePath;
    private Movie mMovie;

    public MovieDetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);
        if (savedInstanceState == null){
            //Log.d(LOG_TAG, "SaveInstanceState is Null");
            Intent intent = getActivity().getIntent();
            mMovie = intent.getParcelableExtra(getString(R.string.parcelable_movie_detail));
        }else{
            //Log.d(LOG_TAG, "SaveInstanceState is not empty");
            mMovie = savedInstanceState.getParcelable(getString(R.string.parcelable_movie_detail));
        }
        mImageBasePath = getString(R.string.movie_poster_base_path);
        TextView movieTitleTextView = (TextView)rootView.findViewById(R.id.textview_movie_title);
        movieTitleTextView.setText(mMovie.getMovieTitle());
        TextView movieReleaseDateTextView = (TextView)rootView.findViewById(R.id.textview_movie_releasedate);
        movieReleaseDateTextView.setText(mMovie.getFormattedMovieReleaseDate());
        TextView movieRatingTextView = (TextView)rootView.findViewById(R.id.textview_movie_rating);
        movieRatingTextView.setText(mMovie.getMovieVoteAverage());
        TextView movieSynopsisTextView = (TextView)rootView.findViewById(R.id.textview_movie_synopsis);
        movieSynopsisTextView.setText(mMovie.getMovieOverview());
        ImageView moviePosterImageView = (ImageView)rootView.findViewById(R.id.imageview_movie_detail_poster);
        Uri imageUri = Uri.parse(mImageBasePath).buildUpon().appendEncodedPath(mMovie.getMoviePosterPath()).build();
        Picasso.with(getActivity().getApplicationContext()).load(imageUri).into(moviePosterImageView);

        return rootView;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(getString(R.string.parcelable_movie_detail), mMovie);
        super.onSaveInstanceState(outState);
    }


}
