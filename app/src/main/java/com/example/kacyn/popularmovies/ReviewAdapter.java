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

public class ReviewAdapter extends CursorAdapter {

    LayoutInflater mLayoutInflater;

    private final String LOG_TAG = ReviewAdapter.class.getSimpleName();

    public ReviewAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);

        mLayoutInflater = LayoutInflater.from(context);
        Log.v(LOG_TAG, "in constructor for review adapter");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        Log.v(LOG_TAG, "In new view");

        View view = mLayoutInflater.inflate(R.layout.list_item_review, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        Log.v(LOG_TAG, "In bind view");

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String author = cursor.getString(DetailActivityFragment.COL_REVIEW_AUTHOR);
        viewHolder.authorView.setText(author);

        String content = cursor.getString(DetailActivityFragment.COL_REVIEW_CONTENT);
        viewHolder.contentView.setText(content);

        Log.v(LOG_TAG, "author: " + author + " content: " + content);

    }

    public static class ViewHolder {
        public final TextView authorView;
        public final TextView contentView;

        public ViewHolder(View view) {
            authorView = (TextView) view.findViewById(R.id.list_item_review_author);
            contentView = (TextView) view.findViewById(R.id.list_item_review_content);
        }
    }

}

/*public class ReviewAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<Review> mReviews = new ArrayList<Review>();
    int mNumReviewsFetched;
    LayoutInflater mLayoutInflater;

    public static class ViewHolder {
        public final TextView authorView;
        public final TextView contentView;
        public final TextView urlView;

        public ViewHolder(View view) {
            authorView = (TextView) view.findViewById(R.id.list_item_review_author);
            contentView = (TextView) view.findViewById(R.id.list_item_review_content);
            urlView = (TextView) view.findViewById(R.id.list_item_review_url);
        }
    }

    public ReviewAdapter(Context c, int numReviewsFetched, ArrayList<Review> reviewsIn) {
        mContext = c;
        mReviews = reviewsIn;

        mNumReviewsFetched = numReviewsFetched;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public void update(int numReviewsFetched, ArrayList<Review> reviewsIn){

        mNumReviewsFetched = numReviewsFetched;
        mReviews = reviewsIn;

        notifyDataSetChanged();
    }

    public int getCount() {
        return mNumReviewsFetched;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = mLayoutInflater.inflate(R.layout.list_item_review, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        viewHolder.authorView.setText(mReviews.get(position).mAuthor);
        viewHolder.contentView.setText(mReviews.get(position).mContent);
        viewHolder.urlView.setText(mReviews.get(position).mUrl);

        return view;
    }

}
*/