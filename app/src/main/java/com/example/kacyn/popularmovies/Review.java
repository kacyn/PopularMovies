package com.example.kacyn.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Kacyn on 10/12/2015.
 */
public class Review implements Parcelable {

    String mAuthor;
    String mContent;
    String mUrl;

    public int describeContents(){
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mAuthor);
        out.writeString(mContent);
        out.writeString(mUrl);
    }

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public Review(String author, String content, String url){
        mAuthor = author;
        mContent = content;
        mUrl = url;
    }

    private Review(Parcel in){
        mAuthor = in.readString();
        mContent = in.readString();
        mUrl = in.readString();
    }
}
