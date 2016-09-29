package com.vng.fresherandroid.mykara.Fragment;

/**
 * Created by Luvi Kaser on 8/3/2016.
 */

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.vng.fresherandroid.mykara.Adapter.FavoriteListSongAdapter;
import com.vng.fresherandroid.mykara.KaraDatabase;
import com.vng.fresherandroid.mykara.R;

/**
 * Customized alert dialog.
 */
public class FavoriteDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setContentView(R.layout.fragment_favorite_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final int idSong = getArguments().getInt(FavoriteListSongAdapter.KEY_ID_SONG);
        final String titleSong = getArguments().getString(FavoriteListSongAdapter.KEY_TITLE_SONG);

        ((TextView) dialog.findViewById(R.id.message)).setText(getString(R.string.message_dialog_favorite1)
                + titleSong
                + getString(R.string.message_dialog_favorite2));

        dialog.findViewById(R.id.positive_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                KaraDatabase dbConnector = new KaraDatabase(getActivity().getBaseContext());
//                dbConnector.setFavorite(idSong, false);

                // Update Firebase DB
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null) {
                    database.getReference(user.getUid())
                            .child(getString(R.string.firebase_child_favorite)).child(String.valueOf(idSong)).removeValue();
                }
                dismiss();
            }

        });

        dialog.findViewById(R.id.cancel_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        dialog.findViewById(R.id.close_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return dialog;

    }

}