package com.example.luvikaser.assistivetouch;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;

public class FloatViewService extends Service {

    private static final String DATA = "PackageNames";
    private static final String CONFIGURATION_CHANGED = "android.intent.action.CONFIGURATION_CHANGED";
    private static final int ABS_ACCELERATION = 4;
    private static final int ICON_ANIM_INTERVAL = 5;
    private WindowManager mWindowManager;
    private ImageView mImageView;                   // ImageView of the float icon
    private WindowManager.LayoutParams mParams;     // Layout params of the float icon
    private GestureDetector mGestureDetector;       // Used to detect on-click event
    private ArrayList<String> mPackageNames;
    private SharedPreferences mSharedPreferences;
    private AnimationTimer mAnimationTimer;

    /**
     * Listen to the screen rotation
     */
    public BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent myIntent) {

            if (myIntent.getAction().equals(CONFIGURATION_CHANGED)) {
                moveIconToSides();
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize package name list
        mPackageNames = new ArrayList<>(Collections.nCopies(Constants.PACKAGE_NUMBER, ""));

        // SharedPreferences used to store data
        mSharedPreferences = getSharedPreferences(DATA, Context.MODE_PRIVATE);

        // Get data from shared preferences
        if (mSharedPreferences != null) {
            for (int i = 0; i < Constants.PACKAGE_NUMBER; ++i) {
                mPackageNames.set(i, mSharedPreferences.getString(i + "", ""));
            }
        }

        // Register broadcast receiver for configuration changed event
        IntentFilter filter = new IntentFilter();
        filter.addAction(CONFIGURATION_CHANGED);
        this.registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ArrayList<String> tmpArray = null;

        // Intent will be null when system automatically restart service
        if (intent != null) {
            tmpArray = intent.getStringArrayListExtra(Constants.MESSAGE_PACKAGE_NAMES);
        }

        // Save data
        if (tmpArray != null) {
            mPackageNames = tmpArray;
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            for (int i = 0; i < Constants.PACKAGE_NUMBER; ++i) {
//                Log.e(i + "", mPackageNames.get(i));
                editor.putString(i + "", mPackageNames.get(i));
            }

            editor.apply();
        }

        // Service has already been started
        if (mImageView != null) {
            return START_STICKY;
        }

        mGestureDetector = new GestureDetector(this, new SingleTapConfirm());
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        mImageView = new ImageView(this);
        mImageView.setImageResource(R.mipmap.ic_launcher);

        mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        mParams.gravity = Gravity.TOP | Gravity.LEFT;
        mParams.x = 0;
        mParams.y = 0;

        mImageView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            private DisplayMetrics mDisplayMetrics;     // Store screen size

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                // Get screen size
                mDisplayMetrics = new DisplayMetrics();
                mWindowManager.getDefaultDisplay().getMetrics(mDisplayMetrics);

                if (mGestureDetector.onTouchEvent(event)) {

                    if (MainActivity.isActive) {
                        Intent intent = new Intent();
                        intent.setAction(Constants.ACTION_CLOSE);
                        sendBroadcast(intent);
                    } else {
                        // On click event, start the main activity
                        Intent intent = new Intent(FloatViewService.this, MainActivity.class);

                        // Use FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS to hide app from recent apps
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        intent.putStringArrayListExtra(Constants.MESSAGE_PACKAGE_NAMES, mPackageNames);
                        startActivity(intent);
                    }
                    return true;
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = mParams.x;
                        initialY = mParams.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_UP:
                        moveIconToSides();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        // Update icon position
                        mParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                        mParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                        mWindowManager.updateViewLayout(mImageView, mParams);

                        if (mAnimationTimer != null) {
                            mAnimationTimer.cancel();
                        }

                        return true;
                }

                return false;
            }
        });

        mWindowManager.addView(mImageView, mParams);

        return START_STICKY;
    }

    /**
     * Move the icon to a side of the screen
     */
    private void moveIconToSides() {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(mDisplayMetrics);

        // Maximum possible x-coordinate
        final int maxX = mDisplayMetrics.widthPixels - mImageView.getWidth();

        // Standardize the coordinates
        mParams.x = Math.min(Math.max(0, mParams.x), maxX);
        mParams.y = Math.min(Math.max(0, mParams.y), mDisplayMetrics.heightPixels - mImageView.getHeight());

        int acceleration;       // Moving acceleration, positive if move to right, negative if move to left

        // Distance to 2 sides of screen
        int d1, d2;
        d1 = mParams.x;
        d2 = maxX - mParams.x;

        // Set the x-coordinate to the nearer side
        if (d1 < d2) {
            acceleration = -ABS_ACCELERATION;
        } else {
            acceleration = ABS_ACCELERATION;
        }

        if (mAnimationTimer == null) {
            mAnimationTimer = new AnimationTimer(2000, ICON_ANIM_INTERVAL);
        }

        mAnimationTimer.updateParams(acceleration, maxX);
        mAnimationTimer.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mImageView != null) {
            mWindowManager.removeView(mImageView);
            mImageView = null;
        }

        if (mAnimationTimer != null) {
            mAnimationTimer.cancel();
            mAnimationTimer = null;
        }

        unregisterReceiver(mBroadcastReceiver);
    }

    /**
     * Used to move icon to side of screen with animation
     */
    class AnimationTimer extends CountDownTimer {

        private int mAcceleration;
        private int mMaxX;
        private int mSpeed;     // Moving speed

        AnimationTimer(long arg0, long arg1) {
            super(arg0, arg1);
            mSpeed = 0;
        }

        public void updateParams(int acceleration, int maxX) {
            mAcceleration = acceleration;
            mMaxX = maxX;
            mSpeed = 0;
        }

        @Override
        public void onTick(long l) {
            mSpeed += mAcceleration;

//            Log.d("mSpeed", mSpeed + " " + l);

            mParams.x += mSpeed;
            mParams.x = Math.min(Math.max(0, mParams.x), mMaxX);
//            Log.w("x", mParams.x + "");

            mWindowManager.updateViewLayout(mImageView, mParams);

            if (mParams.x == 0 || mParams.x == mMaxX) {
                this.cancel();
            }
        }

        @Override
        public void onFinish() {
            if (mParams.x != 0 && mParams.x != mMaxX && mAcceleration != 0) {
                this.start();
            }
        }
    }

    // Used to detect on-click event
    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return true;
        }
    }
}
