package com.vng.fresherandroid.mykara.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vng.fresherandroid.mykara.Fragment.FavoriteDialogFragment;
import com.vng.fresherandroid.mykara.KaraDatabase;
import com.vng.fresherandroid.mykara.MainActivity;
import com.vng.fresherandroid.mykara.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Luvi Kaser on 8/2/2016.
 */
public class FavoriteListSongAdapter extends CursorAdapter {

    public static final String KEY_ID_SONG = "IDSong";
    public static final String KEY_TITLE_SONG = "TitleSong";
    private static final String TAG_DIALOG_FRAGMENT = "FavoriteDialogFragment";
    private static int SHORT_LYRIC_MAX_LENGTH = 30; // Used to shorten lyric
    private LayoutInflater cursorInflater;
    private Context context;

    public FavoriteListSongAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
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
        return cursorInflater.inflate(R.layout.item_favorite_list_songs_layout, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        SwipeLayout swipeLayout = (SwipeLayout)view.findViewById(R.id.swipeLayout);
        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        // TODO: make list items clickable

        TextView tvIDSong = (TextView) view.findViewById(R.id.textViewIDSong);
        TextView tvTitleSong = (TextView) view.findViewById(R.id.textViewTitleSong);
        TextView tvShortLyric = (TextView) view.findViewById(R.id.textViewShortLyricSong);
        ImageView ivFavorite = (ImageView) view.findViewById(R.id.imageViewFavorite);
        final int idSong = cursor.getInt(cursor.getColumnIndex(KaraDatabase.SONGS_COLUMN_ID));
        final String titleSong = cursor.getString(cursor.getColumnIndex(KaraDatabase.SONGS_COLUMN_NAME));
        String fullLyric = cursor.getString(cursor.getColumnIndex(KaraDatabase.SONGS_COLUMN_LYRIC));

        tvIDSong.setText(String.valueOf(idSong));
        tvTitleSong.setText(titleSong);
        tvShortLyric.setText(shortenLyric(fullLyric));

        ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FavoriteDialogFragment dialogFragment = new FavoriteDialogFragment();
                Bundle args = new Bundle();
                args.putInt(KEY_ID_SONG, idSong);
                args.putString(KEY_TITLE_SONG, titleSong);
                dialogFragment.setArguments(args);
                dialogFragment.show(((MainActivity) context).getSupportFragmentManager(), TAG_DIALOG_FRAGMENT);
            }
        });

        animate(view, cursor.getPosition());
    }

    private void animate(View view, final int pos) {
        view.animate().cancel();
        view.setTranslationY(100);
        view.setAlpha(0);
        view.animate().alpha(1.0f).translationY(0).setDuration(300).start();
    }
}
