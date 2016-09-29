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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FilterQueryProvider;
import android.widget.GridView;

import com.vng.fresherandroid.mykara.Adapter.ListSingerAdapter;
import com.vng.fresherandroid.mykara.KaraDatabase;
import com.vng.fresherandroid.mykara.MainActivity;
import com.vng.fresherandroid.mykara.R;
import com.vng.fresherandroid.mykara.SongOfSingerActivity;

import dmax.dialog.SpotsDialog;

public class SingersFragment extends Fragment implements MainActivity.SendTextSearchToFragment {

    private GridView mGridView;
    private Cursor mCursor;
    private ListSingerAdapter mListSingersAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MainActivity) context).setListener(SingersFragment.this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setListener(SingersFragment.this);

        View view = inflater.inflate(R.layout.fragment_singers, container, false);

        mGridView = (GridView) view.findViewById(R.id.singerGridView);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mCursor.moveToPosition(i);

                String singer = mCursor.getString(mCursor.getColumnIndex(KaraDatabase.SONGS_COLUMN_META));

                Intent intent = new Intent(getActivity(), SongOfSingerActivity.class);
                intent.putExtra(MainActivity.KEY_SINGER, singer);
                startActivity(intent);
            }
        });

        new GetSingers("").execute();

        return view;
    }

    @Override
    public void sendData(String textSearch) {
        KaraDatabase dbConnector = new KaraDatabase(getContext());
        mCursor = dbConnector.getSearchSingers(textSearch);
        mListSingersAdapter.changeCursor(mCursor);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
    }

    private class GetSingers extends AsyncTask<Object, Object, Cursor> {

        AlertDialog mProgressDialog = null;
        KaraDatabase mDb = new KaraDatabase(getActivity().getBaseContext());
        String textSearch = null;

        public GetSingers(String textSearch) {
            this.textSearch = textSearch;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = new SpotsDialog(getContext(), getString(R.string.PrgDlg_Title));
            mProgressDialog.show();
        }

        @Override
        protected Cursor doInBackground(Object... objects) {
            return mDb.getSearchSingers(textSearch);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            mCursor = cursor;

            if (mCursor != null) {
                mListSingersAdapter = new ListSingerAdapter(getActivity(), mCursor, 0);
                mGridView.setAdapter(mListSingersAdapter);
            }

            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
}
