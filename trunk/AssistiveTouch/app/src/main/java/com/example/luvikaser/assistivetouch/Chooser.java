package com.example.luvikaser.assistivetouch;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Luvi Kaser on 7/15/2016.
 */
public class Chooser extends Activity {

    private static final String STATE_CHECKED_ITEM = "itemChecked";
    private PackageManager mPackageManager;
    private List<ResolveInfo> mLaunchables = null;
    private ArrayList<String> mExistedPackages;
    private AppAdapter mAdapter = null;
    private boolean[] mItemCheckeds = null;
    private Intent mIntent = null;

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //Restore checked applacions when device is rotated
        mItemCheckeds = savedInstanceState.getBooleanArray(STATE_CHECKED_ITEM);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chooser_layout);

        //Receive list existed applications
        mIntent = getIntent();
        mExistedPackages = mIntent.getStringArrayListExtra(Constants.MESSAGE_EXISTED_PACKAGES);

        //Get all applications on device
        mPackageManager = getPackageManager();
        Intent main = new Intent(Intent.ACTION_MAIN, null);
        main.addCategory(Intent.CATEGORY_LAUNCHER);
        mLaunchables = mPackageManager.queryIntentActivities(main, 0);

        //Remove existed applications from list all applications
        for (Iterator<ResolveInfo> iterator = mLaunchables.iterator(); iterator.hasNext(); ) {
            ResolveInfo resolveInfo = iterator.next();
            if (mExistedPackages.contains(resolveInfo.activityInfo.packageName)) {
                iterator.remove();
            }
        }

        //Sort list applications
        Collections.sort(mLaunchables,
                new ResolveInfo.DisplayNameComparator(mPackageManager));


        //Listener when click button "Done"
        ((Button) findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent newIntent = new Intent();
                //Send position of clicked icon
                newIntent.putExtra(Constants.MESSAGE_POSITION, mIntent.getIntExtra(Constants.MESSAGE_POSITION, 0));

                ArrayList<String> listPackageChoose = new ArrayList<>();
                for (int i = 0; i < mLaunchables.size(); ++i) {
                    if (AppAdapter.itemCheckeds[i]) {
                        listPackageChoose.add(mLaunchables.get(i).activityInfo.packageName);
                    }
                }

                //Send list applications is choosed
                newIntent.putStringArrayListExtra(Constants.MESSAGE_NEW_PACKAGES, listPackageChoose);
                if (listPackageChoose.size() == 0) {
                    setResult(MainActivity.RESULT_CANCELED, newIntent);
                } else {
                    setResult(MainActivity.RESULT_OK, newIntent);
                }

                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mItemCheckeds == null) {
            mItemCheckeds = new boolean[mLaunchables.size()];
        }

        int nChosen = 0;
        for (String s : mExistedPackages) {
            if (s.length() > 0) {
                ++nChosen;
            }
        }

        //Adapter for listView containing list applications (with limit of the number choosing applications)
        mAdapter = new AppAdapter(this, mLaunchables, mPackageManager, mItemCheckeds, Constants.PACKAGE_NUMBER - nChosen);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(mAdapter);

        if (MainActivity.progressDialog != null) {
            MainActivity.progressDialog.dismiss();
            MainActivity.progressDialog = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save checked applacions when device is rotated
        outState.putBooleanArray(STATE_CHECKED_ITEM, AppAdapter.itemCheckeds);
    }
}