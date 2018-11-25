package com.af.simplenotes;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class NotesProvider extends ContentProvider {

    private static final String AUTHORITY = "com.af.plainnotes.notesprovider";
    private static final String BASE_BATH = "notes";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_BATH);

    // Constant to identify the requested operation
    private static final int NOTES = 1;
    private static final int NOTES_ID = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final String CONTENT_ITEM_TYPE = "note";

    static {
        uriMatcher.addURI(AUTHORITY, BASE_BATH, NOTES);
        uriMatcher.addURI(AUTHORITY, BASE_BATH + "/#", NOTES_ID);
    }

    SQLiteDatabase mDatabase;

    @Override
    public boolean onCreate() {

        DBOpenHelper helper = new DBOpenHelper(getContext());
        mDatabase = helper.getWritableDatabase();

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,  String[] selectionArgs,  String sortOrder) {

        if(uriMatcher.match(uri) == NOTES_ID) {
            selection = DBOpenHelper.COLUMN_NOTE_ID + "=" + uri.getLastPathSegment();
        }

        return mDatabase.query(DBOpenHelper.TABLE_NOTES, DBOpenHelper.ALL_COLUMNS, selection,
                null, null, null,
                DBOpenHelper.COLUMN_NOTE_CREATED + " DESC");
    }

    @Override
    public String getType( Uri uri) {
        return null;
    }

    @Override
    public Uri insert( Uri uri,  ContentValues values) {
        long id = mDatabase.insert(DBOpenHelper.TABLE_NOTES, null, values);
        return Uri.parse(BASE_BATH + "/" + id);
    }

    @Override
    public int delete( Uri uri,  String selection,  String[] selectionArgs) {
        return mDatabase.delete(DBOpenHelper.TABLE_NOTES, selection, selectionArgs);
    }

    @Override
    public int update( Uri uri,  ContentValues values,  String selection,  String[] selectionArgs) {
        return mDatabase.update(DBOpenHelper.TABLE_NOTES, values, selection, selectionArgs);
    }
}