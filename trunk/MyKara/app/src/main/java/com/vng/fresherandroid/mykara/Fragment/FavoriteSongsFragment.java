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
import android.widget.FilterQueryProvider;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.vng.fresherandroid.mykara.Adapter.FavoriteListSongAdapter;
import com.vng.fresherandroid.mykara.DetailSongActivity;
import com.vng.fresherandroid.mykara.KaraDatabase;
import com.vng.fresherandroid.mykara.MainActivity;
import com.vng.fresherandroid.mykara.Model.SongItem;
import com.vng.fresherandroid.mykara.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class FavoriteSongsFragment extends Fragment implements MainActivity.SendTextSearchToFragment {

    private FavoriteListSongAdapter mListSongsAdapter = null;
    private ListView mListViewSongs;
    private Cursor mCursor = null;
    private ArrayList<Integer> mFavoriteIds;
    private DatabaseReference mFirebaseRef;
    private AlertDialog mProgressDialog;
    private ValueEventListener mFirebaseListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MainActivity) context).setListener(FavoriteSongsFragment.this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setListener(FavoriteSongsFragment.this);

        View view = inflater.inflate(R.layout.fragment_favorite_songs, container, false);

        mListViewSongs = (ListView) view.findViewById(R.id.listViewFavoriteSongs);
        mListViewSongs.setFastScrollEnabled(true);
        mListViewSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mCursor = mListSongsAdapter.getCursor();
                mCursor.moveToPosition(i);

                int idSong = mCursor.getInt(mCursor.getColumnIndex(KaraDatabase.SONGS_COLUMN_ID));
                String titleSong = mCursor.getString(mCursor.getColumnIndex(KaraDatabase.SONGS_COLUMN_NAME));
                String titleSongClean = mCursor.getString(mCursor.getColumnIndex(KaraDatabase.SONGS_COLUMN_NAME_CLEAN));
                String fullLyric = mCursor.getString(mCursor.getColumnIndex(KaraDatabase.SONGS_COLUMN_LYRIC));
                String singer = mCursor.getString(mCursor.getColumnIndex(KaraDatabase.SONGS_COLUMN_META));

                SongItem songItem = new SongItem(idSong, titleSong, titleSongClean, fullLyric, singer, true);

                Intent intent = new Intent(getActivity(), DetailSongActivity.class);
                intent.putExtra(MainActivity.KEY_SONG_ITEM, songItem);
                startActivity(intent);
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseRef = FirebaseDatabase.getInstance().getReference(user.getUid())
                .child(getString(R.string.firebase_child_favorite));

        mFirebaseListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.e("ValueEventListener", "onDataChange");

                // Get favorite song ids from Firebase db
                if (mFavoriteIds == null) {
                    mFavoriteIds = new ArrayList<>();
                } else {
                    mFavoriteIds.clear();
                }

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    mFavoriteIds.add(Integer.parseInt(data.getKey()));
                }

                KaraDatabase dbConnector = new KaraDatabase(getContext());
                mCursor = dbConnector.getSongsByIDs(mFavoriteIds);

                if (mListSongsAdapter != null) {
                    Log.e("mListSongsAdapter", "not null");
                    mListSongsAdapter.changeCursor(mCursor);
                } else {
                    Log.e("mListSongsAdapter", "null");
                    mListSongsAdapter = new FavoriteListSongAdapter(getActivity(), mCursor, 0);
                    mListViewSongs.setAdapter(mListSongsAdapter);
                }

                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mFirebaseRef.addValueEventListener(mFirebaseListener);

        mProgressDialog = new SpotsDialog(getContext(), getString(R.string.PrgDlg_Title));
        mProgressDialog.show();

        return view;
    }

    @Override
    public void sendData(String textSearch) {
        KaraDatabase dbConnector = new KaraDatabase(getContext());
        mCursor = dbConnector.getSongsByIDs(mFavoriteIds, textSearch);
        mListSongsAdapter.changeCursor(mCursor);
    }

    @Override
    public void onStop() {
        super.onStop();
        mListSongsAdapter = null;
        if (mFirebaseListener != null) {
            mFirebaseRef.removeEventListener(mFirebaseListener);
            mFirebaseListener = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
    }
}
