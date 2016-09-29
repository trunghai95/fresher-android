package com.vng.fresherandroid.mykara.Fragment;

/**
 * Created by Luvi Kaser on 8/3/2016.
 */

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.vng.fresherandroid.mykara.Adapter.FavoriteListSongAdapter;
import com.vng.fresherandroid.mykara.KaraDatabase;
import com.vng.fresherandroid.mykara.MainActivity;
import com.vng.fresherandroid.mykara.R;
import com.vng.fresherandroid.mykara.SplashScreen;

/**
 * Customized alert dialog.
 */
public class LoginFragment extends DialogFragment {

    private static final int RC_SIGN_IN = 3333;
    private EditText mEdtEmail;
    private EditText mEdtPassword;
    private FirebaseAuth mAuth;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setContentView(R.layout.login_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        mEdtEmail = (EditText) dialog.findViewById(R.id.email);
        mEdtPassword = (EditText) dialog.findViewById(R.id.password);

        mAuth = FirebaseAuth.getInstance();

        // When user click login button
        dialog.findViewById(R.id.login_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEdtEmail.getText().toString();
                String password = mEdtPassword.getText().toString();
                if (!email.isEmpty() && !password.isEmpty()) {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(getActivity(), new OnLoginCompleteListener(getString(R.string.message_login_failed)));
                } else {
//                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.message_empty_username_password), Toast.LENGTH_SHORT).show();
                    if (email.isEmpty()) {
                        YoYo.with(Techniques.Shake).duration(500).playOn(mEdtEmail);
                    }

                    if (password.isEmpty()) {
                        YoYo.with(Techniques.Shake).duration(500).playOn(mEdtPassword);
                    }
                }
            }
        });

        // Create new user
        dialog.findViewById(R.id.signup_button).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                String email = mEdtEmail.getText().toString();
                String password = mEdtPassword.getText().toString();
                if (!email.isEmpty() && !password.isEmpty()) {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(getActivity(), new OnLoginCompleteListener(getString(R.string.message_signup_failed)));
                } else {
//                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.message_empty_username_password), Toast.LENGTH_SHORT).show();
                    if (email.isEmpty()) {
                        YoYo.with(Techniques.Shake).duration(500).playOn(mEdtEmail);
                    }

                    if (password.isEmpty()) {
                        YoYo.with(Techniques.Shake).duration(500).playOn(mEdtPassword);
                    }
                }
            }
        });

        // Log in with Google
        dialog.findViewById(R.id.login_google_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(((SplashScreen)getActivity()).mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        return dialog;
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(getActivity(), new OnLoginCompleteListener(getString(R.string.message_login_failed)));
            } else {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.message_login_failed), Toast.LENGTH_SHORT).show();
            }
        }
    }

    class OnLoginCompleteListener implements OnCompleteListener<AuthResult> {

        private String toastText;

        OnLoginCompleteListener(String text) {
            super();
            toastText = text;
        }

        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
                getActivity().finish();
            } else {
                Toast.makeText(getActivity().getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
            }
        }
    }
}