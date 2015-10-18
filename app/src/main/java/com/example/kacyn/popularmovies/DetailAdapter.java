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

    LayoutInflater mLayoutInflater;

    private final String LOG_TAG = DetailAdapter.class.getSimpleName();

    public DetailAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);

        mLayoutInflater = LayoutInflater.from(context);
        Log.v(LOG_TAG, "in constructor for detail adapter");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        Log.v(LOG_TAG, "In new view");

        View view = mLayoutInflater.inflate(R.layout.list_item_detail, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        Log.v(LOG_TAG, "In bind view");

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        Picasso.with(context).load(cursor.getString(DetailActivityFragment.COL_DETAIL_POSTER_URL)).into(viewHolder.posterView);

        String title = cursor.getString(DetailActivityFragment.COL_DETAIL_TITLE);
        viewHolder.titleView.setText(title);

        double voteAvg = cursor.getDouble(DetailActivityFragment.COL_DETAIL_VOTE_AVERAGE);
        viewHolder.voteAvgView.setText("Vote Average: " + voteAvg);

        String releaseDate = cursor.getString(DetailActivityFragment.COL_DETAIL_RELEASE_DATE);
        viewHolder.releaseDateView.setText("Release Date: " + releaseDate);

        String synopsis = cursor.getString(DetailActivityFragment.COL_DETAIL_SYNOPSIS);
        viewHolder.synopsisView.setText(synopsis);

        Log.v(LOG_TAG, "title: " + title);
    }

    public static class ViewHolder {
        public final ImageView posterView;
        public final TextView titleView;
        public final TextView voteAvgView;
        public final TextView releaseDateView;
        public final TextView synopsisView;

        public ViewHolder(View view) {
            posterView = (ImageView) view.findViewById(R.id.poster_image);
            titleView = (TextView) view.findViewById(R.id.title_text);
            voteAvgView = (TextView) view.findViewById(R.id.vote_avg_text);
            releaseDateView = (TextView) view.findViewById(R.id.release_date_text);
            synopsisView = (TextView) view.findViewById(R.id.synopsis_text);
        }
    }
}
