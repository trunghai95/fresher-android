package com.vng.fresherandroid.mykara;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vng.fresherandroid.mykara.Model.SongItem;

public class DetailSongActivity extends AppCompatActivity {
    private android.support.v7.app.ActionBar mActionBar;
    private CardView mViewIDSong;
    private CardView mViewTitleSong;
    private CardView mViewSinger;
    private CardView mViewLyric;
    private ImageView mIsFavorite;
    private TextView mIDSong;
    private TextView mTitleSong;
    private TextView mSinger;
    private TextView mFullLyric;
    private boolean check;
    private DatabaseReference mFirebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_song);

        mActionBar = getSupportActionBar();
        mIDSong = (TextView) findViewById(R.id.textViewIDSong);
        mTitleSong = (TextView) findViewById(R.id.textViewTitleSong);
        mSinger = (TextView) findViewById(R.id.textViewSinger);
        mFullLyric = (TextView) findViewById(R.id.textViewFullLyric);
        mViewIDSong = (CardView) findViewById(R.id.cardViewID);
        mViewTitleSong = (CardView) findViewById(R.id.cardViewTitle);
        mViewSinger = (CardView) findViewById(R.id.cardViewSinger);
        mViewLyric = (CardView) findViewById(R.id.cardViewLyric);

        Intent intent = getIntent();
        final SongItem songItem = (SongItem) intent.getSerializableExtra(MainActivity.KEY_SONG_ITEM);

//        KaraDatabase dbConnector = new KaraDatabase(this);
//        Cursor cursor = dbConnector.getSongByID(songItem.getmIDSong());
//        if (cursor != null){
//            cursor.moveToFirst();
////            boolean isFavorite = !cursor.getString(cursor.getColumnIndex(KaraDatabase.SONGS_COLUMN_FAVORITE)).equals("0");
////            songItem.setmIsFavorite(isFavorite);
//            cursor.close();
//        }

        mIDSong.setText(String.valueOf(songItem.getmIDSong()));
        mTitleSong.setText(songItem.getmTitleSong());
        mSinger.setText(getString(R.string.singer_label) + " " + songItem.getmSinger());
        mFullLyric.setText(songItem.getmFullLyric());

        // Show info on action bar
        mActionBar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.action_bar_home, null);
        mIsFavorite = (ImageView) v.findViewById(R.id.imageViewFavorite);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseRef = FirebaseDatabase.getInstance().getReference(user.getUid())
                .child(getString(R.string.firebase_child_favorite));

        mFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(String.valueOf(songItem.getmIDSong()))) {
                    mIsFavorite.setImageResource(R.drawable.hearts_filled_white);
                } else {
                    mIsFavorite.setImageResource(R.drawable.hearts_white);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mActionBar.setCustomView(v);

        mIsFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (check) {
//                    mIsFavorite.setImageResource(R.drawable.hearts_white);
//                    check = false;
//                } else {
//                    mIsFavorite.setImageResource(R.drawable.hearts_filled_white);
//                    check = true;
//                }

                mFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(String.valueOf(songItem.getmIDSong()))) {
                            mFirebaseRef.child(String.valueOf(songItem.getmIDSong())).removeValue();
                        } else {
                            mFirebaseRef.child(String.valueOf(songItem.getmIDSong())).setValue(songItem.getmIDSong());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        Animation animationLeft = AnimationUtils.loadAnimation(this, R.anim.animation_start_from_left);
        Animation animationRight = AnimationUtils.loadAnimation(this, R.anim.animation_start_from_right);

        mViewIDSong.setAnimation(animationLeft); mViewIDSong.startAnimation(animationLeft);
        mViewTitleSong.setAnimation(animationLeft); mViewTitleSong.startAnimation(animationLeft);
        mViewSinger.setAnimation(animationLeft); mViewSinger.startAnimation(animationLeft);

        mViewLyric.setAnimation(animationRight); mViewIDSong.startAnimation(animationRight);

    }
}
