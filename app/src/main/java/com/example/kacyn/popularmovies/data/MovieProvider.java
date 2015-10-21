package com.example.kacyn.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Kacyn on 10/9/2015.
 */
public class MovieProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int MOVIE = 100;
    static final int MOVIE_WITH_ID = 101;
    static final int REVIEW = 200;
    static final int REVIEW_WITH_MOVIE_ID = 201;
    static final int TRAILER = 300;
    static final int TRAILER_WITH_MOVIE_ID = 301;

    //TODO: add joins here
    private static final SQLiteQueryBuilder sMovieWithReviewsQueryBuilder;
    private static final SQLiteQueryBuilder sMovieWithTrailersQueryBuilder;
    private static final SQLiteQueryBuilder sMovieByIdQueryBuilder;

    static {
        sMovieWithReviewsQueryBuilder = new SQLiteQueryBuilder();
        sMovieWithTrailersQueryBuilder = new SQLiteQueryBuilder();
        sMovieByIdQueryBuilder = new SQLiteQueryBuilder();

        sMovieByIdQueryBuilder.setTables(MovieContract.MovieEntry.TABLE_NAME);
        sMovieWithReviewsQueryBuilder.setTables(MovieContract.ReviewEntry.TABLE_NAME);
        sMovieWithTrailersQueryBuilder.setTables(MovieContract.TrailerEntry.TABLE_NAME);
    }

    //TODO: getMovieByFavoritesSetting

    private static final String sMovieIdSelection =
            MovieContract.MovieEntry.TABLE_NAME +
                    "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ";

    private static final String sTrailerMovieIdSelection =
            MovieContract.TrailerEntry.TABLE_NAME +
                    "." + MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ? ";

    private static final String sReviewMovieIdSelection =
            MovieContract.ReviewEntry.TABLE_NAME +
                    "." + MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ? ";

    private Cursor getMovieByMovieId(Uri uri, String[] projection, String sortOrder) {
        int id = MovieContract.MovieEntry.getMovieIdFromUri(uri);

        String[] selectionArgs;

        selectionArgs = new String[]{"" + id};

        return sMovieByIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMovieIdSelection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getReviewByMovieId(Uri uri, String[] projection, String sortOrder) {
        int movieId = MovieContract.ReviewEntry.getMovieIdFromUri(uri);

        Log.v("Review adapter", "parsed out movie id: " + movieId);

        return sMovieWithReviewsQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sReviewMovieIdSelection,
                new String[]{Integer.toString(movieId)},
                null,
                null,
                sortOrder);
    }

    private Cursor getTrailerByMovieId(Uri uri, String[] projection, String sortOrder) {
        int movieId = MovieContract.TrailerEntry.getMovieIdFromUri(uri);

        return sMovieWithTrailersQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sTrailerMovieIdSelection,
                new String[]{Integer.toString(movieId)},
                null,
                null,
                sortOrder);
    }


    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        //TODO: check this
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_WITH_ID);
        matcher.addURI(authority, MovieContract.PATH_TRAILER, TRAILER);
        matcher.addURI(authority, MovieContract.PATH_TRAILER + "/#", TRAILER_WITH_MOVIE_ID);
        matcher.addURI(authority, MovieContract.PATH_REVIEW, REVIEW);
        matcher.addURI(authority, MovieContract.PATH_REVIEW + "/#", REVIEW_WITH_MOVIE_ID);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch(match) {
            case MOVIE_WITH_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case REVIEW_WITH_MOVIE_ID:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case TRAILER_WITH_MOVIE_ID:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case REVIEW:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case TRAILER:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }



    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {
            case MOVIE_WITH_ID: {
                retCursor = getMovieByMovieId(uri, projection, sortOrder);

                if(retCursor == null){
                    Log.v("Detail ada", "in query, cursor is null");
                }
                else Log.v("Detail adapter", "in query, cursor is valid");


                Log.v("Detail adapter", "return cursor 0th element: " + retCursor.moveToFirst());
                break;
            }

            case REVIEW_WITH_MOVIE_ID: {
                retCursor = getReviewByMovieId(uri, projection, sortOrder);
                if(retCursor == null){
                    Log.v("Review Adapter", "in query, cursor is null");
                }
                else Log.v("Review Adapter", "in query, cursor is valid");

                Log.v("Review adapter", "review uri: " + uri);

                Log.v("Review adapter", "return cursor 0th element: " + retCursor.moveToFirst());
                break;
            }

            case TRAILER_WITH_MOVIE_ID: {
                retCursor = getTrailerByMovieId(uri, projection, sortOrder);
                if(retCursor == null){
                    Log.v("Review trailer Adapter", "in query, cursor is null");
                }
                else Log.v("Review trailer Adapter", "in query, cursor is valid");


                Log.v("Review trailer adapter", "return cursor 0th element: " + retCursor.moveToFirst());
                break;
            }

            case MOVIE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                if(retCursor == null){
                    Log.v("Movie Adapter", "in query, cursor is null");
                }
                else Log.v("Movie Adapter", "in query, cursor is valid");
            }
            break;
            case TRAILER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
            }
            break;
            case REVIEW: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
            }
            break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }


        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                }
                else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case TRAILER: {
                long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.TrailerEntry.buildTrailerUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case REVIEW: {
                long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.ReviewEntry.buildReviewUri(_id);
                }
                else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRAILER:
                rowsDeleted = db.delete(
                        MovieContract.TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEW:
                rowsDeleted = db.delete(
                        MovieContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.v("movie provider", "in update");

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIE:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);

                Log.v("movie provider", "movie updated");
                break;
            case TRAILER:
                rowsUpdated = db.update(MovieContract.TrailerEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case REVIEW:
                rowsUpdated = db.update(MovieContract.ReviewEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount;

        switch (match) {
            case MOVIE:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case TRAILER:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case REVIEW:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
