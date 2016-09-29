package com.vng.fresherandroid.mykara.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.vng.fresherandroid.mykara.KaraDatabase;
import com.vng.fresherandroid.mykara.R;

/**
 * Created by Luvi Kaser on 8/2/2016.
 */
public class ListSongAdapter extends CursorAdapter {
    private static int SHORT_LYRIC_MAX_LENGTH = 30; // Used to shorten lyric
    private LayoutInflater cursorInflater;

    public ListSongAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Trim lyric and add "..."
     */
    private String shortenLyric(String fullLyric) {
        if (fullLyric.length() <= SHORT_LYRIC_MAX_LENGTH) {
            return fullLyric;
        }

        return fullLyric.substring(0, SHORT_LYRIC_MAX_LENGTH) + "...";
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return cursorInflater.inflate(R.layout.item_list_songs_layout, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvIDSong = (TextView) view.findViewById(R.id.textViewIDSong);
        TextView tvTitleSong = (TextView) view.findViewById(R.id.textViewTitleSong);
        TextView tvShortLyric = (TextView) view.findViewById(R.id.textViewShortLyricSong);

        int idSong = cursor.getInt(cursor.getColumnIndex(KaraDatabase.SONGS_COLUMN_ID));
        String titleSong = cursor.getString(cursor.getColumnIndex(KaraDatabase.SONGS_COLUMN_NAME));
        String fullLyric = cursor.getString(cursor.getColumnIndex(KaraDatabase.SONGS_COLUMN_LYRIC));

        tvIDSong.setText(String.valueOf(idSong));
        tvTitleSong.setText(titleSong);
        tvShortLyric.setText(shortenLyric(fullLyric));

        animate(view, cursor.getPosition());
    }

    private void animate(View view, final int pos) {
        view.animate().cancel();
        view.setTranslationY(100);
        view.setAlpha(0);
        view.animate().alpha(1.0f).translationY(0).setDuration(300).start();
    }
}
