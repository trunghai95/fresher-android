package com.vng.fresherandroid.mykara.Fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.vng.fresherandroid.mykara.Adapter.ListVFSongAdaper;
import com.vng.fresherandroid.mykara.DetailSongActivity;
import com.vng.fresherandroid.mykara.KaraDatabase;
import com.vng.fresherandroid.mykara.MainActivity;
import com.vng.fresherandroid.mykara.Model.SongItem;
import com.vng.fresherandroid.mykara.R;
import com.vng.fresherandroid.mykara.Widget.IndexableListView;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;


public class VietSongsFragment extends Fragment implements MainActivity.SendTextSearchToFragment {

    private ListVFSongAdaper mListSongsAdapter = null;
    private IndexableListView mListViewSongs;
    private List<SongItem> mListSongs = new ArrayList<SongItem>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("VietSongs", "onCreateView");
        ((MainActivity) getActivity()).setListener(VietSongsFragment.this);

        View view = inflater.inflate(R.layout.fragment_viet_songs, container, false);
        mListViewSongs = (IndexableListView) view.findViewById(R.id.listViewVietSongs);
        mListViewSongs.setFastScrollEnabled(true);
        mListViewSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SongItem songItem = (SongItem)mListViewSongs.getItemAtPosition(i);
                Intent intent = new Intent(getActivity(), DetailSongActivity.class);

                intent.putExtra(MainActivity.KEY_SONG_ITEM, songItem);
                startActivity(intent);
            }
        });

        if (((MainActivity)getActivity()).mListVietSongs.size() != 0) {
            mListSongs.clear();
            mListSongs.addAll(((MainActivity) getActivity()).mListVietSongs);
            mListSongsAdapter = new ListVFSongAdaper(getActivity(), mListSongs,
                    ((MainActivity) getActivity()).mVietSections,
                    ((MainActivity) getActivity()).mVietStartPositions);
            mListViewSongs.setAdapter(mListSongsAdapter);
        } else {
            new LoadData().execute();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mListViewSongs.setSelection(0);
    }

    @Override
    public void sendData(String textSearch) {
        mListSongsAdapter.filter(textSearch);
        mListViewSongs.setAdapter(mListSongsAdapter);
    }

    private class LoadData extends AsyncTask<Object, Object, Void> {
        private AlertDialog mProgressDialog;
        private KaraDatabase dbConnector = new KaraDatabase(getContext());

        private char lowerCase(char c){
            if (c >='A' && c <= 'Z'){
                c = (char) (c + ('a' - 'A'));
            }
            return c;
        }

        private String GetData(Cursor cursorSongs, ArrayList<SongItem> mListSongs, Integer[] mStartPositions) {
            if (cursorSongs == null){
                return null;
            }

            cursorSongs.moveToFirst();
            int start = -1;
            String mSections = "";

            while (!cursorSongs.isAfterLast()){
                int idSong = cursorSongs.getInt(cursorSongs.getColumnIndex(KaraDatabase.SONGS_COLUMN_ID));
                String titleSong = cursorSongs.getString(cursorSongs.getColumnIndex(KaraDatabase.SONGS_COLUMN_NAME));
                String titleSongClean = cursorSongs.getString(cursorSongs.getColumnIndex(KaraDatabase.SONGS_COLUMN_NAME_CLEAN));
                String singer = cursorSongs.getString(cursorSongs.getColumnIndex(KaraDatabase.SONGS_COLUMN_META));
                String fullLyric = cursorSongs.getString(cursorSongs.getColumnIndex(KaraDatabase.SONGS_COLUMN_LYRIC));
                boolean isFavorite = !cursorSongs.getString(cursorSongs.getColumnIndex(KaraDatabase.SONGS_COLUMN_FAVORITE)).equals("0");

                SongItem songItem = new SongItem(idSong, titleSong, titleSongClean, fullLyric, singer, isFavorite);
                mListSongs.add(songItem);

                char c = titleSongClean.charAt(0);
                if (c >='0' && c <= '9' && mSections.equals("")){
                    mSections = "#";
                    mStartPositions[++start] = 0;
                } else if (lowerCase(c) >= 'a' && lowerCase(c) <= 'z'
                        && (mSections.equals("") || lowerCase(c) != mSections.charAt(start))){
                    mSections = mSections + lowerCase(c);
                    mStartPositions[++start] = cursorSongs.getPosition();
                }

                cursorSongs.moveToNext();
            }

            cursorSongs.close();
            return mSections;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = new SpotsDialog(getContext(), getString(R.string.PrgDlg_Title));
            mProgressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Object... params) {
            Cursor mVietCursor = dbConnector.getSongsByLanguage(KaraDatabase.LANGUAGE_VN);
            ((MainActivity)getActivity()).mVietSections = GetData(mVietCursor, ((MainActivity)getActivity()).mListVietSongs, ((MainActivity)getActivity()).mVietStartPositions);
            return null;
        }



        @Override
        protected void onPostExecute(Void result) {
            mListSongs.addAll(((MainActivity) getActivity()).mListVietSongs);
            mListSongsAdapter = new ListVFSongAdaper(getActivity(), mListSongs,
                    ((MainActivity) getActivity()).mVietSections,
                    ((MainActivity) getActivity()).mVietStartPositions);
            mListViewSongs.setAdapter(mListSongsAdapter);
            mProgressDialog.dismiss();
        }
    }
}
