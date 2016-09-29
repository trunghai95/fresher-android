package com.vng.fresherandroid.mykara;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.TextView;

import com.vng.fresherandroid.mykara.Adapter.ListSongAdapter;
import com.vng.fresherandroid.mykara.Model.SongItem;
import com.vng.fresherandroid.mykara.Widget.IndexableListView;

import dmax.dialog.SpotsDialog;

public class SongOfSingerActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private ListSongAdapter mListSongsAdapter = null;
    private ListView mListViewSongs;
    private Cursor mCursor = null;
    private String singer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_of_singer);

        mListViewSongs = (ListView) findViewById(R.id.listViewSongsOfSinger);
        mListViewSongs.setFastScrollEnabled(true);

        mListViewSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mCursor.moveToPosition(i);

                int idSong = mCursor.getInt(mCursor.getColumnIndex(KaraDatabase.SONGS_COLUMN_ID));
                String titleSong = mCursor.getString(mCursor.getColumnIndex(KaraDatabase.SONGS_COLUMN_NAME));
                String titleSongClean = mCursor.getString(mCursor.getColumnIndex(KaraDatabase.SONGS_COLUMN_NAME_CLEAN));

                String fullLyric = mCursor.getString(mCursor.getColumnIndex(KaraDatabase.SONGS_COLUMN_LYRIC));
                String singer = mCursor.getString(mCursor.getColumnIndex(KaraDatabase.SONGS_COLUMN_META));
                boolean isFavorite = !mCursor.getString(mCursor.getColumnIndex(KaraDatabase.SONGS_COLUMN_FAVORITE)).equals("0");

                SongItem songItem = new SongItem(idSong, titleSong, titleSongClean, fullLyric, singer, isFavorite);

                Intent intent = new Intent(getBaseContext(), DetailSongActivity.class);
                intent.putExtra(MainActivity.KEY_SONG_ITEM, songItem);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        singer = intent.getStringExtra(MainActivity.KEY_SINGER);
        new GetSongsOfSinger(singer, "").execute();

        // Set custom action bar
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.action_bar_home, null);
        TextView titleActionBar = (TextView) v.findViewById(R.id.myTitle);
        titleActionBar.setText(singer);
        getSupportActionBar().setCustomView(v);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(
                new ComponentName(this, MainActivity.class)));
        searchView.setIconifiedByDefault(false);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // User pressed the search button
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mListSongsAdapter.getFilter().filter(newText);
        mListSongsAdapter.notifyDataSetChanged();
        return false;
    }

    private class GetSongsOfSinger extends AsyncTask<Object, Object, Cursor> {
        AlertDialog progressDialog = null;
        KaraDatabase dbConnector = new KaraDatabase(getBaseContext());
        String singer;
        String textSearch = null;

        public GetSongsOfSinger(String singer, String textSearch) {
            this.singer = singer;
            this.textSearch = textSearch;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new SpotsDialog(SongOfSingerActivity.this, getString(R.string.PrgDlg_Title));
            progressDialog.show();
        }

        @Override
        protected Cursor doInBackground(Object... params) {
            return dbConnector.getSongsBySinger(singer, textSearch);
        }

        @Override
        protected void onPostExecute(Cursor result) {
            mCursor = result;
            if (mCursor != null) {
                mListSongsAdapter = new ListSongAdapter(getBaseContext(), mCursor, 0);
                mListViewSongs.setAdapter(mListSongsAdapter);
                mListSongsAdapter.setFilterQueryProvider(new FilterQueryProvider() {
                    @Override
                    public Cursor runQuery(CharSequence charSequence) {
                        return (mCursor = dbConnector.getSongsBySinger(singer, charSequence.toString()));
                    }
                });
            }
            progressDialog.dismiss();
        }
    }
}
