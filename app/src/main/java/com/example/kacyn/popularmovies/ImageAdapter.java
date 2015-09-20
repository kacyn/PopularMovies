package com.example.kacyn.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Kacyn on 9/19/2015.
 */
public class ImageAdapter extends BaseAdapter{
    private Context mContext;
    private ArrayList<String> mImageURLs = new ArrayList<String>();
    int mNumMoviesFetched;

    public ImageAdapter(Context c, int numMoviesFetched, ArrayList<String> urlsIn) {
        mContext = c;
        mImageURLs = urlsIn;

        mNumMoviesFetched = numMoviesFetched;
    }

    public void update(ArrayList<String> urlsIn){
        mImageURLs = urlsIn;

        notifyDataSetChanged();
    }

    public int getCount() {
        return mNumMoviesFetched;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;

        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        //check URLs are present before attempting to load images
        if (!mImageURLs.isEmpty()) {
            Picasso.with(mContext).load(mImageURLs.get(position)).into(imageView);
        }

        return imageView;
    }
}
