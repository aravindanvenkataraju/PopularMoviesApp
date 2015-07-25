package com.aravindan.android.popularmoviesapp;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Aravindan on 17/7/2015.
 */
public class Movie implements Parcelable{
    private String LOG_TAG = getClass().getName();
    private String mMovieID;
    private String mMovieTitle;
    private String mMoviePosterPath;
    private String mMovieReleaseDate;
    private String mMovieVoteAverage;
    private String mMovieOverview;

    Movie(String movieID, String movieTitle, String moviePosterPath, String movieReleaseDate, String movieVoteAverage, String movieOverview){
        mMovieID = movieID;
        mMovieTitle = movieTitle;
        mMoviePosterPath = moviePosterPath;
        mMovieReleaseDate = movieReleaseDate;
        mMovieVoteAverage = movieVoteAverage;
        mMovieOverview = movieOverview;
    }

    private Movie(Parcel parcel){
        mMovieID = parcel.readString();
        mMovieTitle = parcel.readString();
        mMoviePosterPath = parcel.readString();
        mMovieReleaseDate = parcel.readString();
        mMovieVoteAverage = parcel.readString();
        mMovieOverview = parcel.readString();
    }

    public String getMovieID() {
        return mMovieID;
    }

    public String getMovieTitle() {
        return mMovieTitle;
    }

    public String getMoviePosterPath() {
        return mMoviePosterPath;
    }

    public void setMovieID(String mMovieID) {
        this.mMovieID = mMovieID;
    }

    public void setMovieTitle(String mMovieTitle) {
        this.mMovieTitle = mMovieTitle;
    }

    public void setMoviePosterPath(String mMoviePosterPath) {
        this.mMoviePosterPath = mMoviePosterPath;
    }

    public String getMovieReleaseDate() {
        return mMovieReleaseDate;
    }

    public void setMovieReleaseDate(String mMovieReleaseDate) {
        this.mMovieReleaseDate = mMovieReleaseDate;
    }

    public String getMovieVoteAverage() {
        return mMovieVoteAverage+" / 10";
    }

    public void setMovieVoteAverage(String mMovieVoteAverage) {
        this.mMovieVoteAverage = mMovieVoteAverage;
    }

    public String getMovieOverview() {
        return mMovieOverview;
    }

    public void setMovieOverview(String mMovieOverview) {
        this.mMovieOverview = mMovieOverview;
    }

    public String getFormattedMovieReleaseDate(){
        DateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd");
        try{
            if (mMovieReleaseDate == null) {
                return "-";
            }else {
                Date dateValue = sourceFormat.parse(mMovieReleaseDate);
                DateFormat targetFormat = new SimpleDateFormat("MMM dd, yyyy");
                String returnString = targetFormat.format(dateValue);
                return returnString;
            }
        }catch(ParseException e1){
            Log.e(LOG_TAG, e1.getMessage(), e1);
            return "-";
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mMovieID);
        dest.writeString(mMovieTitle);
        dest.writeString(mMoviePosterPath);
        dest.writeString(mMovieReleaseDate);
        dest.writeString(mMovieVoteAverage);
        dest.writeString(mMovieOverview);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }

    };
}
