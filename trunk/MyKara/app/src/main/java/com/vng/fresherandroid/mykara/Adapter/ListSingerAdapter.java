package com.vng.fresherandroid.mykara.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.vng.fresherandroid.mykara.KaraDatabase;
import com.vng.fresherandroid.mykara.R;

/**
 * Created by haibt on 8/3/2016.
 */
public class ListSingerAdapter extends CursorAdapter {

    private static final int MAX_NAME_LENGTH = 20;          // Used to shorten singer names
    private LayoutInflater mCursorInflater;

    public ListSingerAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mCursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * If singer name's length is too long, trim it and add "..."
     */
    private String shortenName(String fullName) {
        if (fullName.length() <= MAX_NAME_LENGTH) {
            return fullName;
        }

        return fullName.substring(0, MAX_NAME_LENGTH) + "...";
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return mCursorInflater.inflate(R.layout.item_singers_layout, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTv = (TextView) view.findViewById(R.id.name);

        String name = shortenName(
                cursor.getString(cursor.getColumnIndex(KaraDatabase.SONGS_COLUMN_META)));

        nameTv.setText(name);

        animate(view, cursor.getPosition());
    }

    private void animate(View view, final int pos) {
        view.animate().cancel();
        view.setTranslationY(100);
        view.setAlpha(0);
        view.animate().alpha(1.0f).translationY(0).setDuration(300).start();
    }
}