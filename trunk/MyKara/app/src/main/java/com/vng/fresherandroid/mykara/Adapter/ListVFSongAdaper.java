package com.vng.fresherandroid.mykara.Adapter;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.vng.fresherandroid.mykara.Model.SongItem;
import com.vng.fresherandroid.mykara.R;

import java.io.UnsupportedEncodingException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Luvi Kaser on 8/8/2016.
 */
public class ListVFSongAdaper extends ArrayAdapter<SongItem> implements SectionIndexer{
    private static int SHORT_LYRIC_MAX_LENGTH = 30; // Used to shorten lyric
    private Context mContext;
    private List<SongItem> mListSongs = new ArrayList<SongItem>();
    private String mSections = "";
    private Integer[] mStartSections = new Integer[27];
    private List<SongItem> mListSongsAll = new ArrayList<SongItem>();
    private String mSectionsAll = "";
    private Integer[] mStartSectionsAll = new Integer[27];

    /**
     * Trim lyric and add "..."
     */
    private String shortenLyric(String fullLyric) {
        if (fullLyric.length() <= SHORT_LYRIC_MAX_LENGTH) {
            return fullLyric;
        }

        return fullLyric.substring(0, SHORT_LYRIC_MAX_LENGTH) + "...";
    }

    public ListVFSongAdaper(Context context, List<SongItem> listSongs, String sections, Integer[] startSections){
        super(context, 0, listSongs);
        mContext = context;
        mListSongs = listSongs;
        mSections = sections;
        System.arraycopy(startSections, 0, mStartSections, 0, 27);

        mListSongsAll.addAll(mListSongs);
        mSectionsAll = mSections;
        System.arraycopy(mStartSections, 0, mStartSectionsAll, 0, 27);

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.item_list_songs_layout, parent, false);

        TextView tvIDSong = (TextView) convertView.findViewById(R.id.textViewIDSong);
        TextView tvTitleSong = (TextView) convertView.findViewById(R.id.textViewTitleSong);
        TextView tvShortLyric = (TextView) convertView.findViewById(R.id.textViewShortLyricSong);
        SongItem songItem = mListSongs.get(position);

        tvIDSong.setText(String.valueOf(songItem.getmIDSong()));
        tvTitleSong.setText(songItem.getmTitleSong());
        tvShortLyric.setText(shortenLyric(songItem.getmFullLyric()));

        animate(convertView, position);

        return convertView;
    }

    private void animate(View view, final int pos) {
        view.animate().cancel();
        view.setTranslationY(100);
        view.setAlpha(0);
        view.animate().alpha(1.0f).translationY(0).setDuration(300).start();
    }

    private char lowerCase(char c){
        if (c >='A' && c <= 'Z'){
            c = (char) (c + ('a' - 'A'));
        }

        return c;
    }

    public void filter(String textSearch){
        mListSongs.clear();
        mSections = "";

        if (textSearch.equals("")){
            mListSongs.addAll(mListSongsAll);
            mSections = mSectionsAll;
            System.arraycopy(mStartSectionsAll, 0, mStartSections, 0, 27);
        } else{
            int start = -1;

            for(SongItem songItem : mListSongsAll){
                if (songItem.getmTitleSong().contains(textSearch)){
                    mListSongs.add(songItem);

                    char c = songItem.getmTitleSongClean().charAt(0);
                    if (start < 26) {
                        if (c >= '0' && c <= '9' && mSections.equals("")) {
                            mSections = "#";
                            mStartSections[++start] = 0;
                        } else if (lowerCase(c) >= 'a' && lowerCase(c) <= 'z'
                                && (mSections.equals("") || lowerCase(c) != mSections.charAt(start))) {
                            mSections = mSections + lowerCase(c);
                            mStartSections[++start] = getPosition(songItem);
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getPositionForSection(int section) {
        return mStartSections[section];
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    @Override
    public Object[] getSections() {
        String[] sections = new String[mSections.length()];

        for (int i = 0; i < mSections.length(); i++) {
            sections[i] = String.valueOf(mSections.charAt(i));
        }

        return sections;
    }

}
