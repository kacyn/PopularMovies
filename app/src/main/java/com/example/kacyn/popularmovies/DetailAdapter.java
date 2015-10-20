package com.example.kacyn.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Kacyn on 10/17/2015.
 */
public class DetailAdapter extends CursorAdapter{

    private static final int VIEW_TYPE_DETAIL = 0;
    private static final int VIEW_TYPE_REVIEW = 1;
    private static final int VIEW_TYPE_TRAILER = 2;
    private static final int VIEW_TYPE_COUNT = 3;

    LayoutInflater mLayoutInflater;
    private final long mNumReviews;

    private final String LOG_TAG = DetailAdapter.class.getSimpleName();

    public DetailAdapter(Context context, Cursor c, int flags, long numReviews) {
        super(context, c, flags);

        mLayoutInflater = LayoutInflater.from(context);
        mNumReviews = numReviews;
    }

    @Override
    public int getItemViewType(int position) {

        if(position == 0) {
            return VIEW_TYPE_DETAIL;
        }
        else if(position < (mNumReviews + 1)) {
            return VIEW_TYPE_REVIEW;
        }
        else {
            return VIEW_TYPE_TRAILER;
        }
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        Log.v(LOG_TAG, "In new view");

        int viewType = getItemViewType(cursor.getPosition());

        int layoutId;
        View view;

        switch (viewType) {
            case VIEW_TYPE_DETAIL:
                layoutId = R.layout.list_item_detail;
                view = mLayoutInflater.inflate(layoutId, parent, false);
                DetailViewHolder detailViewHolder = new DetailViewHolder(view);
                view.setTag(detailViewHolder);
                return view;
            case VIEW_TYPE_REVIEW:
                layoutId = R.layout.list_item_review;
                view = mLayoutInflater.inflate(layoutId, parent, false);
                ReviewViewHolder reviewViewHolder = new ReviewViewHolder(view);
                view.setTag(reviewViewHolder);
                return view;
            case VIEW_TYPE_TRAILER:
                layoutId = R.layout.list_item_trailer;
                view = mLayoutInflater.inflate(layoutId, parent, false);
                TrailerViewHolder trailerViewHolder = new TrailerViewHolder(view);
                view.setTag(trailerViewHolder);
                return view;
            default:
                return null;
        }
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        Log.v(LOG_TAG, "In bind view");

        int viewType = getItemViewType(cursor.getPosition());

        switch (viewType) {
            case VIEW_TYPE_DETAIL:
                DetailViewHolder detailViewHolderiewHolder = (DetailViewHolder) view.getTag();

                Picasso.with(context).load(cursor.getString(DetailActivityFragment.COL_DETAIL_POSTER_URL)).into(detailViewHolderiewHolder.posterView);

                String title = cursor.getString(DetailActivityFragment.COL_DETAIL_TITLE);
                detailViewHolderiewHolder.titleView.setText(title);

                double voteAvg = cursor.getDouble(DetailActivityFragment.COL_DETAIL_VOTE_AVERAGE);
                detailViewHolderiewHolder.voteAvgView.setText("Vote Average: " + voteAvg);

                String releaseDate = cursor.getString(DetailActivityFragment.COL_DETAIL_RELEASE_DATE);
                detailViewHolderiewHolder.releaseDateView.setText("Release Date: " + releaseDate);

                String synopsis = cursor.getString(DetailActivityFragment.COL_DETAIL_SYNOPSIS);
                detailViewHolderiewHolder.synopsisView.setText(synopsis);
                break;

            case VIEW_TYPE_REVIEW:
                ReviewViewHolder reviewViewHolder = (ReviewViewHolder) view.getTag();

                String author = cursor.getString(DetailActivityFragment.COL_REVIEW_AUTHOR);
                reviewViewHolder.authorView.setText(author);

                String content = cursor.getString(DetailActivityFragment.COL_REVIEW_CONTENT);
                reviewViewHolder.contentView.setText(content);

                break;
            case VIEW_TYPE_TRAILER:
                TrailerViewHolder trailerViewHolder = (TrailerViewHolder) view.getTag();

                trailerViewHolder.nameView.setText("Trailer " + cursor.getPosition());
                break;
            default:
                break;
        }
    }

    public static class DetailViewHolder {
        public final ImageView posterView;
        public final TextView titleView;
        public final TextView voteAvgView;
        public final TextView releaseDateView;
        public final TextView synopsisView;

        public DetailViewHolder(View view) {
            posterView = (ImageView) view.findViewById(R.id.poster_image);
            titleView = (TextView) view.findViewById(R.id.title_text);
            voteAvgView = (TextView) view.findViewById(R.id.vote_avg_text);
            releaseDateView = (TextView) view.findViewById(R.id.release_date_text);
            synopsisView = (TextView) view.findViewById(R.id.synopsis_text);
        }
    }

    public static class ReviewViewHolder {
        public final TextView authorView;
        public final TextView contentView;

        public ReviewViewHolder(View view) {
            authorView = (TextView) view.findViewById(R.id.list_item_review_author);
            contentView = (TextView) view.findViewById(R.id.list_item_review_content);
        }
    }

    public static class TrailerViewHolder {
        public final TextView nameView;

        public TrailerViewHolder(View view) {
            nameView = (TextView) view.findViewById(R.id.list_item_trailer_name);
        }
    }
}
