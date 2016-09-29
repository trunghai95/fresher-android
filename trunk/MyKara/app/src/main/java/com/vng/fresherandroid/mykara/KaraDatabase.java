package com.vng.fresherandroid.mykara;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;

/**
 * Created by haibt on 8/2/2016.
 */
public class KaraDatabase extends SQLiteAssetHelper {

    public static final String DATABASE_NAME = "KaraokeVietnam.sqlite";
    public static final int DATABASE_VERSION = 1;
    public static final String SONGS_TABLE_NAME = "ZSONG";
    public static final String SONGS_COLUMN_ID = "Z_PK";
    public static final String SONGS_COLUMN_PK_AS_ID = SONGS_COLUMN_ID + " as _id";
    public static final String SONGS_COLUMN_NAME = "ZSNAME";
    public static final String SONGS_COLUMN_NAME_CLEAN = "ZSNAMECLEAN";
    public static final String SONGS_COLUMN_LANGUAGE = "ZSLANGUAGE";
    public static final String SONGS_COLUMN_META = "ZSMETA";
    public static final String SONGS_COLUMN_META_CLEAN = "ZSMETACLEAN";
    public static final String SONGS_COLUMN_FAVORITE = "ZISFAVORITE";
    public static final String SONGS_COLUMN_LYRIC = "ZSLYRIC";
    public static final String LANGUAGE_VN = "vn";
    public static final String LANGUAGE_EN = "en";
    public static final String ORDERED = " collate nocase asc";

    public static final String SELECT_SONGS_STATEMENT = "select "
            + SONGS_TABLE_NAME + ".*,"
            + SONGS_COLUMN_PK_AS_ID
            + " from " + SONGS_TABLE_NAME;

    public KaraDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public Cursor getSongsByLanguage(String language) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectStatement = SELECT_SONGS_STATEMENT
                + " where " + SONGS_COLUMN_LANGUAGE + " = \'" + language + "\'"
                + " order by " + SONGS_COLUMN_NAME_CLEAN + ORDERED;

        Cursor result = db.rawQuery(selectStatement, null);

        return result;
    }

    public Cursor getSongsByLanguage(String language, String searchStr) {
        if (searchStr.equals("")) {
            return getSongsByLanguage(language);
        }
        SQLiteDatabase db = this.getReadableDatabase();

        String selectStatement = SELECT_SONGS_STATEMENT
                + " where " + SONGS_COLUMN_LANGUAGE + " = \'" + language + "\'"
                + " and (" + SONGS_COLUMN_NAME + " like \'%" + searchStr + "%\'"
                + " or " + SONGS_COLUMN_NAME_CLEAN + " like \'%" + searchStr + "%\')"
                + " order by " + SONGS_COLUMN_NAME_CLEAN + ORDERED;

        Cursor result = db.rawQuery(selectStatement, null);

        return result;
    }

    public Cursor getSongsBySinger(String singerName) {

        SQLiteDatabase db = this.getReadableDatabase();

        String selectStatement = SELECT_SONGS_STATEMENT
                + " where " + SONGS_COLUMN_META + " = \'" + singerName + "\'"
                + " order by " + SONGS_COLUMN_NAME_CLEAN + ORDERED;

        Cursor result = db.rawQuery(selectStatement, null);

        return result;
    }

    public Cursor getSongsBySinger(String singerName, String searchStr) {
        if (searchStr.equals("")) {
            return getSongsBySinger(singerName);
        }
        SQLiteDatabase db = this.getReadableDatabase();

        String selectStatement = SELECT_SONGS_STATEMENT
                + " where " + SONGS_COLUMN_META + " = \'" + singerName + "\'"
                + " and (" + SONGS_COLUMN_NAME + " like \'%" + searchStr + "%\'"
                + " or " + SONGS_COLUMN_NAME_CLEAN + " like \'%" + searchStr + "%\')"
                + " order by " + SONGS_COLUMN_NAME_CLEAN + ORDERED;

        Cursor result = db.rawQuery(selectStatement, null);

        return result;
    }

    public Cursor getAllSingers() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor result = db.rawQuery("select "
                + SONGS_COLUMN_META + ","
                + SONGS_COLUMN_PK_AS_ID
                + " from " + SONGS_TABLE_NAME + " group by " + SONGS_COLUMN_META, null);

        return result;
    }

    public Cursor getSearchSingers(String searchStr) {
        if (searchStr.equals("")) {
            return getAllSingers();
        }
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor result = db.rawQuery("select "
                + SONGS_COLUMN_META + ","
                + SONGS_COLUMN_PK_AS_ID
                + " from " + SONGS_TABLE_NAME
                + " where " + SONGS_COLUMN_META + " like \'%" + searchStr + "%\'"
                + " or " + SONGS_COLUMN_META_CLEAN + " like \'%" + searchStr + "%\'"
                + " group by " + SONGS_COLUMN_META, null);

        return result;
    }

    public Cursor getFavoriteSongs() {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectStatement = SELECT_SONGS_STATEMENT
                + " where " + SONGS_COLUMN_FAVORITE + " = 1"
                + " order by " + SONGS_COLUMN_NAME_CLEAN + ORDERED;

        Cursor result = db.rawQuery(selectStatement, null);

        return result;
    }

    public Cursor getFavoriteSongs(String searchStr) {
        if (searchStr.equals("")) {
            return getFavoriteSongs();
        }
        SQLiteDatabase db = this.getReadableDatabase();

        String selectStatement = SELECT_SONGS_STATEMENT
                + " where " + SONGS_COLUMN_FAVORITE + " = 1"
                + " and (" + SONGS_COLUMN_NAME + " like \'%" + searchStr + "%\'"
                + " or " + SONGS_COLUMN_NAME_CLEAN + " like \'%" + searchStr + "%\')"
                + " order by " + SONGS_COLUMN_NAME_CLEAN + ORDERED;

        Cursor result = db.rawQuery(selectStatement, null);

        return result;
    }

    public Cursor getSongByID(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        String selectStatement = SELECT_SONGS_STATEMENT
                + " where " + SONGS_COLUMN_ID + " = "
                + id;

        Cursor result = db.rawQuery(selectStatement, null);

        return result;
    }

    public Cursor getSongsByIDs(ArrayList<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return null;
        }

        SQLiteDatabase db = this.getReadableDatabase();

        String selectStatement = SELECT_SONGS_STATEMENT + " where " + SONGS_COLUMN_ID + " in (";
        for (int id: ids) {
            selectStatement = selectStatement + id + ",";
        }

        if (ids.size() > 0) {
            selectStatement = selectStatement.substring(0, selectStatement.length() - 1) + ")";
        }

        selectStatement = selectStatement + " order by " + SONGS_COLUMN_NAME_CLEAN + ORDERED;

//        Log.e("selectStatement", selectStatement);

        Cursor result = db.rawQuery(selectStatement, null);

        return result;
    }

    public Cursor getSongsByIDs(ArrayList<Integer> ids, String searchStr) {
        if (searchStr.equals("")) {
            return getSongsByIDs(ids);
        }

        SQLiteDatabase db = this.getReadableDatabase();

        String selectStatement = SELECT_SONGS_STATEMENT + " where " + SONGS_COLUMN_ID + " in (";
        for (int id: ids) {
            selectStatement = selectStatement + id + ",";
        }

        if (ids.size() > 0) {
            selectStatement = selectStatement.substring(0, selectStatement.length() - 1) + ")";
        }

        selectStatement = selectStatement
                + " and (" + SONGS_COLUMN_NAME + " like \'%" + searchStr + "%\'"
                + " or " + SONGS_COLUMN_NAME_CLEAN + " like \'%" + searchStr + "%\')"
                + " order by " + SONGS_COLUMN_NAME_CLEAN + ORDERED;

//        Log.e("selectStatement", selectStatement);

        Cursor result = db.rawQuery(selectStatement, null);

        return result;
    }

    public int setFavorite(int songId, boolean newFavorite) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues newValues = new ContentValues();
        newValues.put(SONGS_COLUMN_FAVORITE, newFavorite ? 1 : 0);

        return db.update(SONGS_TABLE_NAME,
                newValues,
                SONGS_COLUMN_ID + " = " + songId,
                null);
    }
}
