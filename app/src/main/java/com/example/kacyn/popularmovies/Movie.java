package com.example.kacyn.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Kacyn on 9/19/2015.  Custom class to hold Movie data
 */
public class Movie implements Parcelable {

    String mTitle;
    String mReleaseDate;
    double mVoteAvg;
    String mSynopsis;
    String mPosterUrl;

    public int describeContents(){
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mTitle);
        out.writeString(mReleaseDate);
        out.writeDouble(mVoteAvg);
        out.writeString(mSynopsis);
        out.writeString(mPosterUrl);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public Movie(String title, String releaseDate, double voteAvg, String synopsis, String posterUrl){
        mTitle = title;
        mReleaseDate = releaseDate;
        mVoteAvg = voteAvg;
        mSynopsis = synopsis;
        mPosterUrl = posterUrl;
    }

    private Movie(Parcel in){
        mTitle = in.readString();
        mReleaseDate = in.readString();
        mVoteAvg = in.readDouble();
        mSynopsis = in.readString();
        mPosterUrl = in.readString();
    }

}
