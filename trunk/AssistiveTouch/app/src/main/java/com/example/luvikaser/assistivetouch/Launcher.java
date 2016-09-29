package com.example.luvikaser.assistivetouch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Launcher activity is used to start activity
 * on the first run. Don't do any UI task here.
 */
public class Launcher extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finish();

        Intent intent = new Intent(this, FloatViewService.class);
        startService(intent);
    }
}
