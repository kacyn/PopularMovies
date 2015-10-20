package com.example.kacyn.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Kacyn on 10/12/2015.
 */

public class TrailerAdapter extends CursorAdapter {

    LayoutInflater mLayoutInflater;

    private final String LOG_TAG = TrailerAdapter.class.getSimpleName();

    public TrailerAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);

        mLayoutInflater = LayoutInflater.from(context);
        Log.v(LOG_TAG, "in constructor for trailer adapter");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        Log.v(LOG_TAG, "In new view");

        View view = mLayoutInflater.inflate(R.layout.list_item_trailer, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        Log.v(LOG_TAG, "In bind view");

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.nameView.setText("Trailer " + 1);


        /*String author = cursor.getString(DetailActivityFragment.COL_TRAILER_);
        viewHolder.authorView.setText(author);

        String content = cursor.getString(DetailActivityFragment.COL_REVIEW_CONTENT);
        viewHolder.contentView.setText(content);

        Log.v(LOG_TAG, "author: " + author + " content: " + content);*/
    }

    public static class ViewHolder {
        public final TextView nameView;

        public ViewHolder(View view) {
            nameView = (TextView) view.findViewById(R.id.list_item_trailer_name);
        }
    }

}


/*public class TrailerAdapter extends BaseAdapter {

    private static String LOG_TAG = TrailerAdapter.class.getSimpleName();

    private Context mContext;
    private ArrayList<String> mTrailerUrls = new ArrayList<String>();
    int mNumTrailersFetched;
    LayoutInflater mLayoutInflater;

    public static class ViewHolder {
        public final TextView nameView;

        public ViewHolder(View view) {
            nameView = (TextView) view.findViewById(R.id.list_item_trailer_name);
        }
    }

    public TrailerAdapter(Context c, int numTrailersFetched, ArrayList<String> trailerUrlsIn) {
        mContext = c;
        mTrailerUrls = trailerUrlsIn;

        mNumTrailersFetched = numTrailersFetched;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public void update(int numTrailersFetched, ArrayList<String> trailerUrlsIn){

        mNumTrailersFetched = numTrailersFetched;

        Log.v("Trailer adapter", "num trailers fetched: " + mNumTrailersFetched);

        mTrailerUrls = trailerUrlsIn;

        notifyDataSetChanged();
    }

    public int getCount() {
        Log.v(LOG_TAG, "num trailers fetched in get count: " + mNumTrailersFetched);
        return mNumTrailersFetched;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = mLayoutInflater.inflate(R.layout.list_item_trailer, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        Log.v(LOG_TAG, "in get view, position: " + (position + 1));

        viewHolder.nameView.setText("Trailer " + (position + 1));

        return view;
    }

}
*/