package com.vng.fresherandroid.mykara.Model;

import java.io.Serializable;

/**
 * Created by Luvi Kaser on 8/2/2016.
 */

/**
 * Store detail information of a song.
 */
public class SongItem implements Serializable {
    private int mIDSong;
    private String mTitleSong;
    private String mTitleSongClean;
    private String mFullLyric;
    private String mSinger;
    private boolean mIsFavorite;

    public SongItem(int mIDSong, String mTitleSong, String mTitleSongClean, String mFullLyric, String mSinger, boolean mIsFavorite) {
        this.mIDSong = mIDSong;
        this.mTitleSong = mTitleSong;
        this.mFullLyric = mFullLyric;
        this.mSinger = mSinger;
        this.mIsFavorite = mIsFavorite;
        this.mTitleSongClean = mTitleSongClean;
    }

    public int getmIDSong() {
        return mIDSong;
    }

    public void setmIDSong(int mIDSong) {
        this.mIDSong = mIDSong;
    }

    public String getmTitleSong() {
        return mTitleSong;
    }

    public void setmTitleSong(String mTitleSong) {
        this.mTitleSong = mTitleSong;
    }

    public String getmTitleSongClean() {
        return mTitleSongClean;
    }

    public void setmTitleSongClean(String mTitleSongClean) {
        this.mTitleSongClean = mTitleSongClean;
    }

    public String getmFullLyric() {
        return mFullLyric;
    }

    public void setmFullLyric(String mFullLyric) {
        this.mFullLyric = mFullLyric;
    }

    public String getmSinger() {
        return mSinger;
    }

    public void setmSinger(String mSinger) {
        this.mSinger = mSinger;
    }

    public boolean ismIsFavorite() {
        return mIsFavorite;
    }

    public void setmIsFavorite(boolean mIsFavorite) {
        this.mIsFavorite = mIsFavorite;
    }
}
