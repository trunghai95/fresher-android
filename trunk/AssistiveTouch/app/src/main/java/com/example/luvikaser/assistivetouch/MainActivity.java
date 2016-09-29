package com.example.luvikaser.assistivetouch;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Luvi Kaser on 7/15/2016.
 */

public class MainActivity extends Activity {

    public static final int MY_REQUEST_CODE = 12345;
    public static final float SCREEN_RATIO = 0.6f;
    private ArrayList<ImageView> mImageList;
    private ArrayList<String> mPackageNames;
    private PackageManager mPackageManager;
    private DisplayMetrics mDisplayMetrics;
    public static boolean isActive;
    public static ProgressDialog progressDialog;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.ACTION_CLOSE)) {
                finish();
            }
        }
    };

    //Function check positon of pointer inside a view
    private static boolean isPointInsideView(float x, float y, View view) {
        int location[] = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];

        if ((x > viewX && x < (viewX + view.getWidth())) &&
                (y > viewY && y < (viewY + view.getHeight()))) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);

        // Get screen size
        mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);

        // Set window size from screen size
        int size = Math.min(mDisplayMetrics.widthPixels, mDisplayMetrics.heightPixels);
        getWindow().setLayout((int) (size * SCREEN_RATIO), (int) (size * SCREEN_RATIO));

        //Get package manager
        mPackageManager = getPackageManager();

        //Load icons
        mImageList = new ArrayList<>();
        mImageList.add((ImageView) findViewById(R.id.imageView11));
        mImageList.add((ImageView) findViewById(R.id.imageView12));
        mImageList.add((ImageView) findViewById(R.id.imageView13));
        mImageList.add((ImageView) findViewById(R.id.imageView21));
        mImageList.add((ImageView) findViewById(R.id.imageView22));
        mImageList.add((ImageView) findViewById(R.id.imageView23));
        mImageList.add((ImageView) findViewById(R.id.imageView31));
        mImageList.add((ImageView) findViewById(R.id.imageView32));
        mImageList.add((ImageView) findViewById(R.id.imageView33));

        //Get list of existed applications
        Intent intent = getIntent();
        mPackageNames = intent.getStringArrayListExtra(Constants.MESSAGE_PACKAGE_NAMES);

        //Load data and set listeners for icons
        for (int i = 0; i < Constants.PACKAGE_NUMBER; ++i) {
            if (mPackageNames.get(i).length() != 0) {
                try {
                    mImageList.get(i).setImageDrawable(mPackageManager.getApplicationIcon(mPackageNames.get(i)));
                } catch (PackageManager.NameNotFoundException e) {
                    mPackageNames.set(i, "");
                }
            }

            mImageList.get(i).setOnClickListener(new MyOnClickListener(i));

            MyOnLongClickListener myOnLongClickListener = new MyOnLongClickListener(i);
            mImageList.get(i).setOnLongClickListener(myOnLongClickListener);
            mImageList.get(i).setOnTouchListener(myOnLongClickListener);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_CLOSE);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        isActive = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActive = false;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
    }

    //Receive list of choosed applications when click icon not contained application
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                int position = data.getIntExtra(Constants.MESSAGE_POSITION, 0);
                ArrayList<String> newPackages = data.getStringArrayListExtra(Constants.MESSAGE_NEW_PACKAGES);

                //Set first choosed application for the cliked icon
                mPackageNames.set(position, newPackages.get(0));
                try {
                    mImageList.get(position).setImageDrawable(mPackageManager.getApplicationIcon(mPackageNames.get(position)));
                } catch (PackageManager.NameNotFoundException e) {
                    mPackageNames.set(position, "");
                    mImageList.get(position).setImageResource(R.drawable.plussign);
                }

                //Set remaining applications for icons don't contain application
                int i = 1;
                for (int pos = 0; i < Constants.PACKAGE_NUMBER; ++pos) {
                    if (i >= newPackages.size())
                        break;
                    if (mPackageNames.get(pos).length() == 0) {
                        mPackageNames.set(pos, newPackages.get(i));
                        try {
                            mImageList.get(pos).setImageDrawable(mPackageManager.getApplicationIcon(mPackageNames.get(pos)));
                        } catch (PackageManager.NameNotFoundException e) {
//                            Log.e("package", "package name " + mPackageNames.get(pos) + " not found");
                            mPackageNames.set(pos, "");
                            mImageList.get(pos).setImageResource(R.drawable.plussign);
                        }
                        ++i;
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Start service for updating data
        Intent intent = new Intent(this, FloatViewService.class);
        intent.putStringArrayListExtra(Constants.MESSAGE_PACKAGE_NAMES, mPackageNames);
        startService(intent);

        unregisterReceiver(mReceiver);
    }

    //Listener for event when click icon
    private class MyOnClickListener implements View.OnClickListener {

        private int mPosition;

        MyOnClickListener(int pos) {
            mPosition = pos;
        }

        @Override
        public void onClick(View v) {
            //Open application when icon contained application
            if (mPackageNames.get(mPosition).length() != 0) {
                Intent intent = mPackageManager.getLaunchIntentForPackage(mPackageNames.get(mPosition));
                finish();
                startActivity(intent);
            } else {

                progressDialog = ProgressDialog.show(MainActivity.this, "Loading...", "Please wait!");

                //Open list applications for choosing when icon not contained application
                Intent intent = new Intent(getBaseContext(), Chooser.class);
                intent.putExtra(Constants.MESSAGE_POSITION, mPosition);
                intent.putStringArrayListExtra(Constants.MESSAGE_EXISTED_PACKAGES, mPackageNames);
                startActivityForResult(intent, MY_REQUEST_CODE);
            }
        }
    }

    //Listener for event when long lick icon
    private class MyOnLongClickListener implements View.OnLongClickListener, View.OnTouchListener {

        MyOnTouchListener mListener;
        private WindowManager mWindowManager;
        private WindowManager.LayoutParams mParams;
        private ImageView mImageView = null;
        private ImageView mDeleteImage = null;
        private int mPosition;
        private boolean mIsOnDrag = false;
        private int initialX;
        private int initialY;
        private float initialTouchX;
        private float initialTouchY;

        MyOnLongClickListener(int pos) {
            mPosition = pos;
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            if (!mIsOnDrag) { //Get position of pointer when long click and not drag icon
                initialTouchX = motionEvent.getRawX();
                initialTouchY = motionEvent.getRawY();
            } else if (mListener != null) { //Set onTouch for new icon
                mListener.onTouch(mImageView, motionEvent);
            }
            return false;
        }

        @Override
        public boolean onLongClick(View v) {
            // Vibrate device
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(50);

            mIsOnDrag = true;

            //Create new icon for drag when long click icon (set image of old icon is null)
            mImageView = new ImageView(getBaseContext());
            mImageView.setImageDrawable(((ImageView) v).getDrawable());
            ((ImageView) v).setImageDrawable(null);

            mParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);

            mParams.gravity = Gravity.TOP | Gravity.LEFT;

            mParams.x = (int) initialTouchX - v.getWidth() / 2;
            mParams.y = (int) initialTouchY - v.getHeight() / 2;

            initialX = mParams.x;
            initialY = mParams.y;

            //Set listener onTouch for new icon
            mListener = new MyOnTouchListener(v);
            mImageView.setOnTouchListener(mListener);

            //Add new icon
            mWindowManager.addView(mImageView, mParams);

            //Create delete icon when long lick icon
            mDeleteImage = new ImageView(getBaseContext());
            mDeleteImage.setImageResource(R.mipmap.remove);
            WindowManager.LayoutParams mParamsDelete;
            mParamsDelete = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);

            mParamsDelete.gravity = Gravity.TOP | Gravity.LEFT;
            mParamsDelete.x = mDisplayMetrics.widthPixels / 2 - mDeleteImage.getDrawable().getIntrinsicWidth() / 2;
            mParamsDelete.y = mDisplayMetrics.heightPixels - mDeleteImage.getDrawable().getIntrinsicHeight();

            //Add delete icon
            mWindowManager.addView(mDeleteImage, mParamsDelete);
            return true;
        }

        //Listener for event drag new icon
        private class MyOnTouchListener implements View.OnTouchListener {
            private ImageView image;
            private boolean mIsOnDelete = false;

            MyOnTouchListener(View v) {
                image = (ImageView) v;
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        return true;

                    case MotionEvent.ACTION_UP:
                        boolean mOK = true;
                        int pos = 0;
                        for (ImageView mImage : mImageList) {
                            if (isPointInsideView(event.getRawX(), event.getRawY(), mImage)) {
                                if (pos == mPosition)
                                    break;
                                Drawable drawable = mImage.getDrawable();
                                mImage.setImageDrawable(mImageView.getDrawable());
                                image.setImageDrawable(drawable);

                                String mPackageName = mPackageNames.get(mPosition);
                                mPackageNames.set(mPosition, mPackageNames.get(pos));
                                mPackageNames.set(pos, mPackageName);

                                mOK = false;
                                break;
                            }
                            ++pos;
                        }

                        if (isPointInsideView(event.getRawX(), event.getRawY(), mDeleteImage)) {
                            image.setImageResource(R.drawable.plussign);
                            mPackageNames.set(mPosition, "");
                            mOK = false;
                        }

                        if (mOK)
                            image.setImageDrawable(mImageView.getDrawable());
                        mWindowManager.removeView(mImageView);
                        mWindowManager.removeView(mDeleteImage);

                        mIsOnDrag = false;
                        mIsOnDelete = false;
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        if (!mIsOnDelete) {
                            // Move into delete icon area
                            if (isPointInsideView(event.getRawX(), event.getRawY(), mDeleteImage)) {
                                mIsOnDelete = true;

                                // Vibrate device
                                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                vibrator.vibrate(25);
//                                Log.e("vibrate", "vibrate");

                                mDeleteImage.setImageResource(R.mipmap.remove2);
                            }
                        } else {
                            // Move out of delete icon area
                            if (!isPointInsideView(event.getRawX(), event.getRawY(), mDeleteImage)) {
                                mIsOnDelete = false;
                                mDeleteImage.setImageResource(R.mipmap.remove);
                            }
                        }

                        mParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                        mParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                        mWindowManager.updateViewLayout(mImageView, mParams);
                        return true;
                }
                return false;
            }
        }
    }
}
